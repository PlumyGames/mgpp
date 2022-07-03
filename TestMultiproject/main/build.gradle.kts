@file:Suppress("SpellCheckingInspection")

import io.github.liplum.mindustry.*

plugins {
    java
    id("io.github.liplum.mgpp") version "1.0.7"
}
repositories {
    mindustryRepo()
    mavenCentral()
}
apply<MindustryPlugin>()
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
    client {
        mindustry be "22728"
    }
    server {
        mindustry be "22728"
    }
    mods {
        worksWith {
            add fromTask ":core:jar"
        }
    }
    meta += ModMeta(
        name = "main",
        displayName = "Main Mod",
        minGameVersion = "136",
        main = "plumy.test.MainMod"
    )
}
mindustryAssets {
    root at "$projectDir/assets"
}
dependencies {
    implementation(project(":core"))
    importMindustry()
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("com.github.liplum:TestUtils:v0.1")
}
