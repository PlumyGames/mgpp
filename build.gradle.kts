buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://www.jitpack.io")
        }
    }
}
allprojects {
    group = "plumy.mindustry"
    version = "1.0"
    repositories {
        mavenCentral()
        maven {
            url = uri("https://www.jitpack.io")
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform {
            excludeTags("slow")
        }
        testLogging {
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            showStandardStreams = true
        }
    }
}
tasks {
    register("test") {
        group = "verification"
        doLast {
            copy {
                from()
            }
            allprojects.forEach {
                logger.lifecycle("------>[Testing ${it.name}]<------")
                it.tasks.withType<Test>().forEach { test ->
                    try {
                        test.executeTests()
                    } catch (e: Exception) {
                        logger.error(e.toString(),e)
                    }
                }
            }
        }
    }
}