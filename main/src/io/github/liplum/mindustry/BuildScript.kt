@file:JvmName("BuildScript")

package io.github.liplum.mindustry

import io.github.liplum.dsl.afterEvaluateThis
import io.github.liplum.dsl.getOrCreate
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

/**
 * Declare a maven repository of what Mindustry used.
 *
 * You can call this in [RepositoryHandler] closure
 */
fun RepositoryHandler.mindustryRepo(
): MavenArtifactRepository = maven { repo ->
    repo.name = "mindustry-center"
    repo.url = URI("https://www.jitpack.io")
}
/**
 * Import the dependencies of Mindustry.
 * It will take those into account:
 * - [MindustryExtension.projectType]
 * - [DependencySpec.mindustry]
 * - [DependencySpec.arc]
 *
 * You can call this in [DependencyHandler] closure
 */
fun Project.importMindustry() = afterEvaluateThis {
    val ex = extensions.getOrCreate<MindustryExtension>(
        Mgpp.MainExtensionName
    )
    val mdt = ex._dependency.mindustryDependency.get()
    val arc = ex._dependency.arcDependency.get()
    // Mindustry core
    mdt.whenAvailable("core", ::addMindustry)
    // Arc
    arc.whenAvailable("arc-core", ::addMindustry)
    when (ex.projectType.get()) {
        ProjectType.Mod -> {
            mdt.whenAvailable("desktop", ::addMindustry)
            mdt.whenAvailable("server", ::addMindustry)
            // This doesn't work, so disable it for now until a better solution
            //addMindustry(mdt.resolve("backend-headless"))
        }
        ProjectType.Plugin -> {
            mdt.whenAvailable("server", ::addMindustry)
            //addMindustry(mdt.resolve("backend-headless"))
        }
        else -> {}
    }
}
/**
 * Import the dependencies of Mindustry.
 * It will take those into account:
 * - [MindustryExtension.projectType]
 * - [DependencySpec.mindustry]
 * - [DependencySpec.arc]
 *
 * You can call this in [DependencyHandler] closure
 */
fun Project.importMindustry(configurationName: String) = afterEvaluateThis {
    val ex = extensions.getOrCreate<MindustryExtension>(
        Mgpp.MainExtensionName
    )
    val mdt = ex._dependency.mindustryDependency.get()
    val arc = ex._dependency.arcDependency.get()
    fun addSpecificDependency(dependencyNotation: String) {
        addDependency(configurationName, dependencyNotation)
    }
    // Mindustry core
    mdt.whenAvailable("core", ::addSpecificDependency)
    // Arc
    arc.whenAvailable("arc-core", ::addSpecificDependency)
    when (ex.projectType.get()) {
        ProjectType.Mod -> {
            mdt.whenAvailable("desktop", ::addSpecificDependency)
            mdt.whenAvailable("server", ::addSpecificDependency)
            // This doesn't work, so disable it for now until a better solution
            //addMindustry(mdt.resolve("backend-headless"))
        }
        ProjectType.Plugin -> {
            mdt.whenAvailable("server", ::addSpecificDependency)
            //addMindustry(mdt.resolve("backend-headless"))
        }
        else -> {}
    }
}
internal
fun Dependency.tryJitpackResolve(module: String): String? {
    // e.g.:com,github,anuken,arc
    // https://www.jitpack.io/com/github/anuken/arc/arc-core/v135.2/arc-core-v135.2.pom
    val pomUrl = "https://www.jitpack.io/${fullName.split(".").joinToString("/")}/$version/$module-$version.pom"
    val u = URL(pomUrl)
    val huc = u.openConnection() as HttpURLConnection
    huc.requestMethod = "GET"
    huc.connect()
    val code: Int = huc.responseCode
    return if (code == 404) null else resolve(module)
}
internal
fun Project.addMindustry(dependencyNotation: String) {
    compileOnly(dependencyNotation)
    testImplementation(dependencyNotation)
}
internal
fun Project.testImplementation(dependencyNotation: String) {
    addDependency("testImplementation", dependencyNotation)
}
internal
fun Project.compileOnly(dependencyNotation: String) {
    addDependency("compileOnly", dependencyNotation)
}
internal
fun Project.addDependency(configurationName: String, dependencyNotation: String) {
    configurations.getByName(configurationName).dependencies.add(
        dependencies.create(dependencyNotation)
    )
}