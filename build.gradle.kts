allprojects {
    buildscript {
        repositories {
            mavenCentral()
            gradlePluginPortal()
            maven {
                url = uri("https://www.jitpack.io")
            }
        }
    }
}
