rootProject.name = "TestMultiproject"
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
    plugins {
        kotlin("jvm").version("1.9.20")
    }
}

include(
    "main", "core", "js", "lib"
)
