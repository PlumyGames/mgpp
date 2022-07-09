rootProject.name = "TestMultiproject"
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
include(
    "main", "core", "js"
)
