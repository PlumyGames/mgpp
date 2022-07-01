import io.github.liplum.mindustry.*
import io.github.liplum.mindustry.task.AntiAlias
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.archivesName
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://www.jitpack.io")
        }
    }
    dependencies {
        classpath(files(File("../build/libs").listFiles()))
    }
}
sourceSets {
    main {
        java.srcDirs("src")
        java.srcDirs("$buildDir/generated/mindustry")
        resources.srcDir("resources")
    }
    test {
        java.srcDir("test")
        java.srcDirs("$buildDir/generated/mindustry")
        resources.srcDir("resources")
    }
}
apply<MindustryPlugin>()
plugins {
    kotlin("jvm") version "1.7.0"
}

group = "net.liplum"
version = "0.2"

repositories {
    mindustryRepo()
    mavenCentral()
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
            add github "liplum/cyberio"
        }
    }
    meta += ModMeta(
        name = "test-plumy-mindustry-gradle-plugin-kt",
        displayName = "Test Plumy Mindustry Gradle Plugin Kt",
        main = "plumy.test.TestModKt",
        author = "Liplum"
    )
    meta["version"] = "Kotlin 666"
    meta.minGameVersion = "136"
    deploy {
        // fatJar is default option unless you use another tool like shadowJar
        fatJar
    }
}
mindustryAssets {
    sprites {
        dir = rootDir.resolve("sprites")
        dependsOn("antiAlias")
    }
    sprites {
        dir = rootDir.resolve("sprites/data")
        rootAt(rootDir.resolve("sprites"))
        dependsOn("antiAlias")
        genClass
    }
    sounds {
        dir = rootDir.resolve("sounds")
        genClass
    }
}

tasks.named<AntiAlias>("antiAlias") {
    sourceDirectory.set(rootDir.resolve("sprites-raw"))
    destinationDirectory.set(rootDir.resolve("sprites"))
    addFilter {
        it.name != "sender.png"
    }
    //options.isIncremental = false
}

dependencies {
    testImplementation(kotlin("test"))
    importMindustry()
}

tasks.withType<Jar> {
    doLast {
        println("----------------jar----------------")
        files(this).forEach { println(it) }
        println("----------------jar----------------")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}