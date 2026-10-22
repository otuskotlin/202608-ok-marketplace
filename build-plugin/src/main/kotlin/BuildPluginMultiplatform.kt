package ru.otus.otuskotlin.marketplace.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
internal class BuildPluginMultiplatform : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        group = rootProject.group
        version = rootProject.version

        plugins.withId("org.jetbrains.kotlin.multiplatform") {
            extensions.configure<KotlinMultiplatformExtension> {
                configureTargets(this@with)
                sourceSets.configureEach {
                    languageSettings.apply {
//                        languageVersion = "1.9"
                        progressiveMode = true
                        optIn("kotlin.time.ExperimentalTime")
                    }
                }
            }
        }
        repositories {
            mavenCentral()
        }
    }
}

@Suppress("LongMethod", "MagicNumber")
private fun KotlinMultiplatformExtension.configureTargets(project: Project) {
    val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")
    val jvmLanguage = libs.findVersion("jvm-language").get().requiredVersion
    val jvmCompiler = libs.findVersion("jvm-compiler").get().requiredVersion
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(jvmLanguage.toInt()))
//        vendor.set(JvmVendorSpec.AZUL)
    }

    jvm {
        compilations.configureEach {
            compilerOptions.configure {
                jvmTarget.set(JvmTarget.valueOf("JVM_$jvmCompiler"))
            }
        }
    }
    linuxX64()
    macosArm64()
    project.tasks.withType(JavaCompile::class.java) {
        sourceCompatibility = jvmLanguage
        targetCompatibility = jvmCompiler
    }
}
