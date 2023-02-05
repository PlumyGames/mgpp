import io.github.liplum.mindustry.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.0"
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
tasks.register<TestOutputTask>("iconMaker") {
    outputFile.set(rootDir.resolve("icon.png"))
}
mindustry {
    dependency {
        mindustry on "v141.3"
        arc on "v141.3"
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
        json(repo = "BlueWolf3682/Exotic-Mod", branch = "0.8")
        fromTask(path = "iconMaker")
    }
    val modpack2nd = addModpack("number 2") {
        json(repo = "sk7725/TimeControl")
    }
    addModpack("for debugging") {
        `testingUtilities`
        `informatis`
    }
    addClient {
        // anonymous 1
        official(version = "v141.3")
    }
    addClient("debugging") {
        official(version = "v141.3")
        modpack = "for debugging"
    }
    addClient {
        // anonymous 2
        fooClient(tag = "v8.0.0", file = "erekir-client.jar")
        modpack = modpack2nd
    }
    addClient(name = "Old Mindustry") {
        official(version = "v126")
    }
    addServer {
        official(version = "v141.3")
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
    destinationDirectory.set(mindustryAssets.assetsRoot.get().resolve("sprites"))
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

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}