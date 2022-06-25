rootProject.name = "MindustryGradlePluginPlumy"
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    plugins {
        kotlin("jvm").version(extra["kotlinVersion"] as String)
    }
}
include(
    "main"
)