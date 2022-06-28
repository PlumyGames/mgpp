import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import plumy.mindustry.*
import plumy.mindustry.task.AntiAlias
import plumy.mindustry.task.RunMindustry

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://www.jitpack.io")
        }
    }
    dependencies {
        classpath(files("../main/build/libs/PlumyMindustryGradlePlugin-1.0.jar"))
    }
}
sourceSets {
    main {
        java.srcDirs("src")
        java.srcDirs("$buildDir/generated/resourceClass")
        resources.srcDir("resources")
    }
    test {
        java.srcDir("test")
        java.srcDirs("$buildDir/generated/resourceClass")
        resources.srcDir("resources")
    }
}
apply<plumy.mindustry.MindustryPlugin>()
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
        useMirror(version = "d7312445a1")
        arc(version = "123fbf12b9")
    }
    client {
        be(version = "22714")
    }
    server {
        be(version = "22714")
    }
    mods {
        /* This can work
        worksWith(
            GitHub("liplum/cyberio"),
        )
        */
        // also you can
        worksWith {
            github("liplum/cyberio")
        }
    }
    modMeta(
        name = "test-plumy-mindustry-gradle-plugin-kt",
        displayName = "Test Plumy Mindustry Gradle Plugin Kt",
        main = "plumy.test.TestModKt",
        author = "Liplum"
    )
    meta["version"] = "Kotlin 666"
    meta.minGameVersion = "136"
}
mindustryAsset {
    sprites {
        dir = rootDir.resolve("sprites")
        genClass = true
    }
}

tasks.named("genResourceClass"){
    dependsOn("antiAlias")
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

tasks.withType<RunMindustry> {
    dataOnTemporary()
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