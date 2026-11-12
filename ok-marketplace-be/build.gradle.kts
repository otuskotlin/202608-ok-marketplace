plugins {
    alias(libs.plugins.kotlin.jvm) //apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
}

repositories {
    mavenCentral()
}

group = "ru.otus.otuskotlin.marketplace"
version = "0.1.0"

subprojects {
    group = rootProject.group
    version = rootProject.version
    repositories { mavenCentral() }
}

tasks.named("check") {
    group = "verification"
    dependsOn(":ok-marketplace-api-v1-jackson:check", ":ok-marketplace-api-v2-kmp:check")
}

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
