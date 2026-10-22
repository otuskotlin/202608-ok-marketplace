plugins {
    id("build-jvm")
}

val catalogs = extensions.getByType(org.gradle.api.artifacts.VersionCatalogsExtension::class.java)
val libsCatalog = catalogs.named("libs")
val kotlinVersion: String = libsCatalog.findVersion("kotlin").get().requiredVersion

dependencies {
    implementation(libsCatalog.findLibrary("kotlinx-datetime").get())
}
