@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.mindustry.*
import io.github.liplum.mindustry.task.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import java.io.File

typealias Mgpp = MindustryPlugin

class MindustryPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        LocalProperties.clearCache(this)
        val ex = target.extensions.getOrCreate<MindustryExtension>(R.x.mindustry)
        val assets = extensions.getOrCreate<MindustryAssetsExtension>(R.x.mindustryAssets)
        val run = extensions.getOrCreate<RunMindustryExtension>(R.x.runMindustry)
        /**
         * Handle [InheritFromParent].
         * Because they're initialized at the [Plugin.apply] phase, the user-code will overwrite them if it's possible.
         */
        target.parent?.let {
            it.plugins.whenHas<MindustryPlugin> {
                val parentEx = it.extensions.getOrCreate<MindustryExtension>(R.x.mindustry)
                ex._isLib.set(parentEx._isLib)
                ex._dependency.mindustryDependency.set(parentEx._dependency.mindustryDependency)
                ex._dependency.arcDependency.set(parentEx._dependency.arcDependency)
                ex._run._dataDir.set(parentEx._run._dataDir)
                ex._run._forciblyClear.set(parentEx._run._forciblyClear)
                ex._deploy._androidSdkRoot.set(parentEx._deploy._androidSdkRoot)
                ex._deploy.enableFatJar.set(parentEx._deploy.enableFatJar)
            }
        }
        // Register this for dynamically configure tasks without class reference in groovy.
        // Eagerly configure this task in order to be added into task group in IDE
        tasks.register<AntiAlias>("antiAlias") {
            group = R.taskGroup.mindustry
        }.get()
        tasks.register<ModHjsonGenerate>("genModHjson") {
            group = R.taskGroup.mindustry
            modMeta.set(ex._modMeta)
            outputHjson.set(temporaryDir.resolve("mod.hjson"))
        }
        plugins.apply<MindustryAppPlugin>()
        plugins.whenHas<JavaPlugin> {
            plugins.apply<MindustryAssetPlugin>()
            plugins.apply<MindustryJavaPlugin>()
        }
        GroovyBridge.attach(target)
    }

    companion object {
        /**
         * The default check time(ms) for latest version.
         *
         * 1 hour as default.
         */
        const val defaultOutOfDataTime = 1000L * 60 * 60
        /**
         * The check time(ms) for latest version.
         *
         * 1 hour as default.
         */
        var outOfDataTime = defaultOutOfDataTime
        /**
         * [Mindustry official release](https://github.com/Anuken/Mindustry/releases)
         */
        const val MindustryOfficialReleaseURL = "https://github.com/Anuken/Mindustry/releases"
        /**
         * GitHub API of [Mindustry official release](https://api.github.com/repos/Anuken/Mindustry/releases/latest)
         */
        const val APIMindustryOfficialReleaseURL = "https://api.github.com/repos/Anuken/Mindustry/releases"
        /**
         * GitHub API of [Latest Mindustry official release](https://api.github.com/repos/Anuken/Mindustry/releases/latest)
         */
        const val APIMindustryOfficialLatestReleaseURL = "https://api.github.com/repos/Anuken/Mindustry/releases/latest"
        /**
         * GitHub API of [Mindustry bleeding-edge release](https://api.github.com/repos/Anuken/Mindustry/releases/latest)
         */
        const val APIMindustryBEReleaseURL = "https://api.github.com/repos/Anuken/MindustryBuilds/releases/latest"
        /**
         * GitHub API of [Latest Mindustry bleeding-edge release](https://api.github.com/repos/Anuken/Mindustry/releases/latest)
         */
        const val APIMindustryBELatestReleaseURL = "https://api.github.com/repos/Anuken/MindustryBuilds/releases/latest"
        /**
         * [Arc tags](https://github.com/Anuken/Arc/tags)
         */
        const val ArcTagURL = "https://api.github.com/repos/Anuken/arc/tags"
        /**
         * [An *Anime* cat](https://github.com/Anuken)
         */
        const val Anuken = "anuken"
        /**
         * [Mindustry game](https://github.com/Anuken/Mindustry)
         */
        const val Mindustry = "mindustry"
        /**
         * [Mindustry bleeding-edge](https://github.com/Anuken/MindustryBuilds)
         */
        const val MindustryBuilds = "MindustryBuilds"
        /**
         * [The Mindustry repo on Jitpack](https://github.com/anuken/mindustry)
         */
        const val MindustryJitpackRepo = "com.github.anuken.mindustry"
        /**
         * [The mirror repo of Mindustry on Jitpack](https://github.com/anuken/mindustryjitpack)
         */
        const val MindustryJitpackMirrorRepo = "com.github.anuken.mindustryjitpack"
        /**
         * [The GitHub API to fetch the latest commit of mirror](https://github.com/Anuken/MindustryJitpack/commits/main)
         */
        const val MindustryJitpackLatestCommit = "https://api.github.com/repos/Anuken/MindustryJitpack/commits/main"
        /**
         * [The GitHub API to fetch the latest commit of arc](https://github.com/Anuken/Arc/commits/master)
         */
        const val ArcLatestCommit = "https://api.github.com/repos/Anuken/Arc/commits/master"
        /**
         * [The Arc repo on Jitpack](https://github.com/anuken/arc)
         */
        const val ArcJitpackRepo = "com.github.anuken.arc"
        /**
         * An empty folder for null-check
         */
        @JvmStatic
        val DefaultEmptyFile = File("")
    }
}
/**
 * Provides the existing `antiAlias`: [AntiAlias] task.
 */
val TaskContainer.`antiAlias`: TaskProvider<AntiAlias>
    get() = named<AntiAlias>("antiAlias")
/**
 * Provides the existing `genModHjson`: [ModHjsonGenerate] task.
 */
val TaskContainer.`genModHjson`: TaskProvider<ModHjsonGenerate>
    get() = named<ModHjsonGenerate>("genModHjson")

fun String?.addAngleBracketsIfNeed(): String? =
    if (this == null) null
    else if (startsWith("<") && endsWith(">")) this
    else "<$this>"


inline fun safeRun(func: () -> Unit) {
    try {
        func()
    } catch (_: Throwable) {
    }
}
/**
 * Provides the existing [resolveMods][ResolveMods] task.
 */
val TaskContainer.`resolveMods`: TaskProvider<ResolveMods>
    get() = named<ResolveMods>("resolveMods")

