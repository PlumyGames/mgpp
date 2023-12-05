import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    groovy
    `java-gradle-plugin`
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.18.0"
    id("org.jetbrains.dokka") version "1.7.20"
}
group = "io.github.liplum.mgpp"
val mgppVersion: String by project
version = mgppVersion
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
gradlePlugin {
    plugins {
        create("mgpp") {
            id = "io.github.liplum.mgpp"
            displayName = "mgpp"
            description = "For Mindustry modding in Java, kotlin and so on."
            implementationClass = "io.github.liplum.mindustry.MindustryPlugin"
        }
    }
}
pluginBundle {
    website = "https://plumygames.github.io/mgpp/"
    vcsUrl = "https://github.com/PlumyGames/mgpp"
    tags = listOf("mindustry", "mindustry-mod", "mod")
}
tasks.compileGroovy {
    classpath = sourceSets.main.get().compileClasspath
}
tasks.compileKotlin {
    libraries.from(files(sourceSets.main.get().groovy.classesDirectory))
}
val pluginName: String by project
sourceSets {
    main {
        java.srcDirs("src")
        resources.srcDir("resources")
        groovy.srcDir("src")
    }
    test {
        java.srcDir("test")
        resources.srcDir("resources")
        groovy.srcDir("test")
    }
}
val arcVersion: String by project
dependencies {
    compileOnly("com.github.anuken.arc:arc-core:$arcVersion")
    implementation("org.hjson:hjson:3.0.0")
    implementation("com.google.code.gson:gson:2.9.0")
    testImplementation("com.github.anuken.arc:arc-core:$arcVersion")
    testImplementation("org.hjson:hjson:3.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.7.0")
}

tasks.test {
    useJUnitPlatform()
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.named<Jar>("jar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    includeEmptyDirs = false
    from(
        configurations.compileClasspath.get().mapNotNull {
            if (it.isFile && it.extension == "jar"
                && ("arc-core" in it.name)
            )
                zipTree(it)
            else null
        }
    )
}
// NOTE: All artifacts must have the same name.
// If you are using multi-project for plugin publishing, please ensure all `Jar` tasks have the same name.
tasks.withType<Jar> {
    archiveBaseName.set(pluginName)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

java {
    withSourcesJar()
    withJavadocJar()
}