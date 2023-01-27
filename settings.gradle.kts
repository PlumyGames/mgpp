rootProject.name = "MindustryGradlePluginPlumy"
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("jvm").version("1.6.21")
    }
}
include(
    "main"
)