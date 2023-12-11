@file:Suppress("SpellCheckingInspection")

import io.github.liplum.mindustry.*

plugins {
    java
    id("io.github.liplum.mgpp")

}
repositories {
    mindustryRepo()
    mavenCentral()
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
dependencies {
    implementation(project(":core"))
    importMindustry()
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("com.github.liplum:TestUtils:v0.1")
}

version = "1.0"
group = "net.liplum"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

mindustry {
    client {
        mindustry be latest
    }
    mods {
        worksWith {
            add fromTask ":core:jar"
        }
    }
    meta += ModMeta(
        name = "main",
        displayName = "Main Mod",
        minGameVersion = "146",
        main = "plumy.test.MainMod"
    )
}
mindustryAssets {
    root at "$projectDir/assets"
}