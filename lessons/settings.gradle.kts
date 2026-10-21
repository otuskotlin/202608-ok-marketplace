pluginManagement {
    plugins {
        val kotlinVersion: String = providers.gradleProperty("kotlinVersion").get()
        kotlin("jvm") version kotlinVersion
    }
}

rootProject.name = "lessons"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("m1l1-first")
include("m1l2-basic")
include("m1l3-func")
include("m1l4-oop")
include("m2l1-dsl")

include("m2l2-coroutines")
include("m2l3-flows")
include("m2l4-kmp")
include("m2l5-1-interop")
include("m2l5-2-j")
include("m2l6-gradle")
include(
    ":m2l6-gradle:sub1:ssub1",
    ":m2l6-gradle:sub1:ssub2",
    ":m2l6-gradle:sub2",
)