import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    groovy
    `java-gradle-plugin`
    id("com.github.johnrengelman.shadow") version "7.1.2"
}
gradlePlugin {
    plugins {
        create("plumyMindustryPlugin") {
            id = "plumy.mindustry"
            implementationClass = "plumy.mindustry.PlumyMdtPlugin"
        }
    }
}

tasks.named<GroovyCompile>("compileGroovy") {
    val compileKotlin = tasks.named<KotlinCompile>("compileKotlin")
    dependsOn(compileKotlin)
    classpath += files(compileKotlin.get().destinationDirectory)
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
repositories {
    mavenCentral()
}

dependencies {
    shadow("com.github.anuken.arc:arc-core:123fbf12b9")
    shadow("org.hjson:hjson:3.0.0")
    implementation(kotlin("stdlib"))
    implementation("com.github.anuken.arc:arc-core:123fbf12b9")
    implementation("org.hjson:hjson:3.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.test {
    useJUnitPlatform()
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
tasks.named<Jar>("jar") {
    archiveBaseName.set(pluginName)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    includeEmptyDirs = false
}
tasks.named<ShadowJar>("shadowJar") {
    //minimize()
    configurations = listOf(project.configurations.getByName("shadow"))
    archiveBaseName.set(pluginName)
    archiveClassifier.set("")
    archiveVersion.set(project.version.toString())
}