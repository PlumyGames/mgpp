rootProject.name = "mgpp"
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("jvm").version("1.9.20")
    }
}
include(
    "main"
)