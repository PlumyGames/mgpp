import io.github.liplum.mindustry.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.21"
    id("io.github.liplum.mgpp") version "2.0.0"
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

group = "net.liplum"
version = "0.2"

repositories {
    mindustryRepo()
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    importMindustry()
}

open class TestOutputTask : DefaultTask() {
    val outputFile = project.objects.property<File>()
        @OutputFile get

    @TaskAction
    fun test() {
    }
}
// Only for testing TaskMod(taskName)
tasks.register<TestOutputTask>("anyTask") {
    outputFile.set(rootDir.resolve(".gitignore"))
}
mindustry {
    dependency {
        mindustry(version = "v146")
        arc(version = "v146")
    }
    modMeta {
        name = "mgpp-kt"
        displayName = "MGPP Kotlin Test"
        main = "plumy.test.TestModKt"
        author = "Liplum"
    }
    meta["version"] = "Kotlin 666"
    meta.minGameVersion = "136"
}
deployMod {
    baseName = "KotlinMod"
    version = "666"
}
runMindustry {
    addModpack {
        // default modpack
        jvm(repo = "liplum/CyberIO")
        json(repo = "BlueWolf3682/Exotic-Mod", branch = "master")
        fromTask(path = "anyTask")
    }
    val modpack2nd = addModpack("number 2") {
        json(repo = "sk7725/TimeControl")
    }
    addModpack("for debugging") {
        `testingUtilities`
        `informatis`
    }
    addModpack("for debugging") {
        github("EB-wilson/TooManyItems")
    }
    addClient {
        // anonymous 1
        official(version = "v146")
    }
    addClient("inline modpack") {
        official(version = "v146")
        useModpack {
            jvm(repo = "liplum/MultiCrafterLib")
        }
    }
    addClient("debugging") {
        official(version = "v146")
        useModpack(name = "for debugging")
    }
    addClient("custom data dir") {
        official(version = "v146")
        putDataAt(file = buildDir.resolve("customDataDir"))
    }
    addClient("default data dir") {
        official(version = "v146")
        modpack = null
        useDefaultDataDir()
    }
    addClient("from url") {
        url("https://github.com/Anuken/Mindustry/releases/download/v146/Mindustry.jar")
        useModpack("from url") {
            url("https://github.com/liplum/MultiCrafterLib/releases/download/v1.8/MultiCrafterLib-1.8.jar")
        }
    }
    addClient("debugging") {
        official(version = "v146")
        useModpack(name = "for debugging 2")
    }
    addClient {
        // anonymous 2
        fooClient(tag = "v8.0.0", file = "erekir-client.jar")
        useModpack(modpack2nd)
    }
    addClient(name = "Old Mindustry") {
        official(version = "v126")
    }
    addClient(name = "Old Mindustry") {
        official(version = "v136")
    }
    addClient(name = "latest") {
        official(version = latest)
    }
    addClient(name = "From local.properties") {

    }
    addServer {
        modpack = null
        official(version = "v146")
    }
    addServer("latest") {
        modpack = null
        official(version = latest)
    }
}

tasks.dexJar {
    options.minApi = "26"
    options {
        minApi = "14"
    }
}
mindustryAssets {

}

tasks.antiAlias {
    sourceDirectory.set(rootDir.resolve("sprites-raw"))
    destinationDirectory.set(projectDir.resolve("assets").resolve("sprites"))
    addFilter {
        it.name != "sender.png"
    }
    //options.isIncremental = false
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
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}