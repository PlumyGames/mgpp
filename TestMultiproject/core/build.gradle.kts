@file:Suppress("SpellCheckingInspection")
import io.github.liplum.mindustry.*
plugins {
    java
    id("io.github.liplum.mgpp") version "1.0.8"
}
repositories {
    mindustryRepo()
    mavenCentral()
}
dependencies {
    importMindustry()
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("com.github.liplum:TestUtils:v0.1")
}
sourceSets {
    main {
        java.srcDirs("src")
        resources.srcDir("resources")
    }
    test {
        java.srcDir("test")
        resources.srcDir("resources")
    }
}

version = "1.0"
group = "net.liplum"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
mindustry {
    dependency {
        mindustry mirror "d7312445a1"
        arc on "123fbf12b9"
    }
    meta += ModMeta(
        name = "core",
        displayName = "Core Mod",
        minGameVersion = "136",
        main = "plumy.test.CoreMod"
    )
}

