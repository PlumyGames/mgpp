rootProject.name = "mgpp"
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("jvm").version("1.8.22")
    }
}
include(
    "main"
)