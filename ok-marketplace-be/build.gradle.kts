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
}