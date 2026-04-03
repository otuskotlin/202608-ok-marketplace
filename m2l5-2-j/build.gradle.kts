import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.jvm.Jvm
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import javax.inject.Inject

plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    // Description of modules corresponding to our target platforms
    //  common - common code that we can use on different platforms
    //  for each target platform, we can specify our own specific dependencies
    sourceSets {
        val jvmMain by getting {
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

abstract class GenerateJniHeadersTask : DefaultTask() {
    // Инъекция сервиса вместо обращения к project во время выполнения:
    // это делает задачу совместимой с configuration cache
    @get:Inject
    abstract val providers: ProviderFactory

    @get:InputDirectory
    abstract val sourceDirectory: DirectoryProperty

    @get:InputDirectory
    abstract val classesDirectory: DirectoryProperty

    @get:Input
    abstract val javapExecutable: Property<String>

    @get:Input
    abstract val javacExecutable: Property<String>

    @get:OutputDirectory
    abstract val jniHeaderDirectory: DirectoryProperty

    @get:Internal
    abstract val tmpDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val classesDir = classesDirectory.get().asFile
        val jniDir = jniHeaderDirectory.get().asFile
        val tmpDir = tmpDirectory.get().asFile.apply { mkdirs() }
        val javap = javapExecutable.get()
        val javac = javacExecutable.get()

        val bodyExtractingRegex = """^.+\Rpublic \w* ?class ([^\s]+).*\{\R((?s:.+))\}\R$""".toRegex()
        val nativeMethodExtractingRegex = """.*\bnative\b.*""".toRegex()

        classesDir.walkTopDown()
            .filter { "META" !in it.absolutePath }
            .forEach { file ->
                if (!file.isFile) return@forEach
                println("FILE: $file")

                val execOutput = providers.exec {
                    commandLine(javap, "-private", "-cp", classesDir.absolutePath, file.absolutePath)
                }
                execOutput.result.get().assertNormalExitValue()
                val output = execOutput.standardOutput.asText.get()

                val (qualifiedName, methodInfo) = bodyExtractingRegex.find(output)?.destructured ?: return@forEach

                val lastDot = qualifiedName.lastIndexOf('.').takeIf { it >= 0 } ?: return@forEach
                val packageName = qualifiedName.substring(0, lastDot)
                val className = qualifiedName.substring(lastDot + 1, qualifiedName.length)

                val nativeMethods = nativeMethodExtractingRegex
                    .findAll(methodInfo)
                    .map { it.groups }
                    .flatMap { it.asSequence().mapNotNull { group -> group?.value } }
                    .toList()

                if (nativeMethods.isEmpty()) return@forEach

                val source = buildString {
                    appendLine("package $packageName;")
                    appendLine("public class $className {")
                    for (method in nativeMethods) {
                        if ("()" in method) appendLine(method)
                        else {
                            val updatedMethod = StringBuilder(method).apply {
                                var count = 0
                                var i = 0
                                while (i < length) {
                                    if (this[i] == ',' || this[i] == ')') insert(i, " arg${count++}".also { i += it.length + 1 })
                                    else i++
                                }
                            }
                            appendLine(updatedMethod)
                        }
                    }
                    appendLine("}")
                }
                val outputFile = tmpDir.resolve(packageName.replace(".", "/")).apply { mkdirs() }
                    .resolve("$className.java").apply { delete() }.apply { createNewFile() }
                outputFile.writeText(source)

                providers.exec {
                    commandLine(javac, "-h", jniDir.absolutePath, outputFile.absolutePath)
                }.result.get().assertNormalExitValue()
            }
    }
}

tasks {
    val pathToLib: String = project.layout.buildDirectory.dir("c/lib").get().toString()
    println("PATH TO LIB: $pathToLib")

    val javaHome = Jvm.current().javaHome
    val javapPath = javaHome.resolve("bin").walk().firstOrNull { it.name.startsWith("javap") }?.absolutePath
        ?: error("javap not found")
    val javacPath = javaHome.resolve("bin").walk().firstOrNull { it.name.startsWith("javac") }?.absolutePath
        ?: error("javac not found")

    val generateJniHeaders by registering(GenerateJniHeadersTask::class) {
        group = "interop"
        dependsOn("compileKotlinJvm")

        sourceDirectory.set(layout.projectDirectory.dir("src/jvmMain/kotlin"))
        classesDirectory.set(layout.buildDirectory.dir("classes/kotlin/jvm/main"))
        javapExecutable.set(javapPath)
        javacExecutable.set(javacPath)
        jniHeaderDirectory.set(layout.buildDirectory.dir("jniIncludes"))
        tmpDirectory.set(layout.buildDirectory.dir("tmp/jvmJni"))
    }

    val makeCLib by registering(Exec::class) {
        dependsOn(generateJniHeaders)
        group = "build"
        commandLine("make")
        workingDir(layout.projectDirectory.dir("c-jni"))
        // Передаём путь к JDK, под которым запущен Gradle, чтобы Makefile
        // использовал совместимый jni.h (не хардкодим версию JDK)
        environment("JDK", javaHome.absolutePath)
    }

    withType(KotlinJvmTest::class) {
        dependsOn(makeCLib)
        systemProperty("java.library.path", pathToLib)
    }
}
