plugins {
    alias(libs.plugins.kotlin.jvm) //apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
}

repositories {
    mavenCentral()
}

group = "com.otus.otuskotlin.marketplace"
version = "0.0.1"

tasks {
    build {
        println("this is a build setting")
        doFirst {
            println("this a build process")
        }
        doLast {
            println("this is a build post-process")
        }
    }

    val someTask = create("someTask", Task::class) {
        dependsOn(build)
        println("this is a build setting")
    }

    create("someOtherTask", Task::class) {
        dependsOn(someTask)
    }

    register("buildImages") {

        val isLinuxOS = listOf("linux").any { System.getProperty("os.name").lowercase().contains(it) }
        logger.lifecycle("isLinuxOS: $isLinuxOS")


        dependsOn(project("ok-marketplace-tmp").tasks.getByName("build"))

//        dependsOn(project("ok-marketplace-app-spring").tasks.getByName("dockerBuildJvm"))
//        dependsOn(project("ok-marketplace-app-ktor").tasks.getByName("dockerBuildJvm"))
//        dependsOn(project("ok-marketplace-app-ktor").tasks.getByName("dockerBuildLinuxX64"))
//        dependsOn(project("ok-marketplace-app-rabbit").tasks.getByName("dockerBuildJvm"))
//        dependsOn(project("ok-marketplace-app-kafka").tasks.getByName("dockerBuildJvm"))
    }
}