rootProject.name = "MindustryGradlePluginPlumy"
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