dependencyResolutionManagement {
    // Use Maven Central as the default repository (where Gradle will download dependencies) in all subprojects.
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

pluginManagement {
    plugins {
        val kotlinVersion: String by settings
        kotlin("jvm") version kotlinVersion
    }
}

plugins {
    // Use the Foojay Toolchains plugin to automatically download JDKs required by subprojects.
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "otuskotlin-marketplace-202608"

include("m1l1-first")
include("m1l2-basic", "m1l3-func")
include("m1l3-func")
include("m1l4-oop")
include("m2l1-dsl")
include("m2l2-coroutines")