plugins {
    kotlin("jvm")
}

dependencies {
//    implementation(project(":m2l5-gradle:sub1:ssub1"))
//    implementation(project(":m2l5-gradle:sub1:ssub2"))

    implementation(projects.m2l6Gradle.sub1.ssub1)
    implementation(projects.m2l6Gradle.sub1.ssub2)
}

repositories {
    mavenCentral()
}
