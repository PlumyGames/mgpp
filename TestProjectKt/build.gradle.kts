import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import plumy.mindustry.importMindustry
import plumy.mindustry.mindustry
import plumy.mindustry.mindustryRepo
import plumy.mindustry.task.RunMindustryTask

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
        resources.srcDir("resources")
    }
    test {
        java.srcDir("test")
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
    mindustry.set(MirrorDependency(version = "d7312445a1"))
    arc.set(Dependency(ArcRepo, "123fbf12b9"))
    client.set(GameLocation(user = "anuken", repo = "MindustryBuilds", version = "22714", release = "Mindustry-BE-Desktop-22714.jar"))
    mods {
        worksWith(
            GitHub("liplum/cyberio"),
        )
    }
    assets {
        modMeta(
            name = "test-plumy-mindustry-gradle-plugin-kt",
            displayName = "Test Plumy Mindustry Gradle Plugin Kt",
            main = "plumy.test.TestModKt",
            author = "Liplum"
        )
    }
}

dependencies {
    testImplementation(kotlin("test"))
    importMindustry()
}

tasks.withType<RunMindustryTask> {
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