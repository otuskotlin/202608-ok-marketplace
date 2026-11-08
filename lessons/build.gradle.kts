plugins {
    kotlin("jvm") apply false
}

group = "ru.otus.otuskotlin.marketplace"
version = "0.0.1"

repositories {
    mavenCentral()
}

subprojects {
    group = rootProject.group
    version = rootProject.version
}
