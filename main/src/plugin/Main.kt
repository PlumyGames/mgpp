@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.mindustry.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider

class MindustryPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        LocalProperties.clearCache(this)
        val ex = extensions.getOrCreate<MindustryExtension>(R.x.mindustry)
        val assets = extensions.getOrCreate<MindustryAssetsExtension>(R.x.mindustryAssets)
        val run = extensions.getOrCreate<RunMindustryExtension>(R.x.runMindustry)
        val deployX = extensions.getOrCreate<DeployModExtension>(R.x.deployMod)

        parent?.let {
            // disable those if current project is subproject.
            deployX.enableFatJar = false
            deployX.outputMod = false
        }
        /**
         * Handle [InheritFromParent].
         * Because they're initialized at the [Plugin.apply] phase, the user-code will overwrite them if it's possible.
         */
        parent?.let {
            if (it.plugins.hasPlugin<MindustryPlugin>()) {
                val parentEx = it.extensions.getOrCreate<MindustryExtension>(R.x.mindustry)
                ex._dependency.mindustryDependency.set(parentEx._dependency.mindustryDependency)
                ex._dependency.arcDependency.set(parentEx._dependency.arcDependency)
            }
        }
        // Register this for dynamically configure tasks without class reference in groovy.
        // Eagerly configure this task in order to be added into task group in IDE

        tasks.register<ModHjsonGenerate>(R.task.genModHjson) {
            group = R.taskGroup.mindustry
            modMeta.set(ex._modMeta)
            outputHjson.set(temporaryDir.resolve("mod.hjson"))
        }
        if (plugins.hasPlugin<JavaPlugin>()) {
            plugins.apply<MindustryJavaPlugin>()
        } else {
            plugins.apply<MindustryJsonPlugin>()
        }
        // Set the convention to ex._deploy
        deployX._baseName.convention(provider {
            ex._modMeta.get().name
        })
        deployX._version.convention(provider {
            ex._modMeta.get().version
        })
        plugins.apply<MindustryAppPlugin>()
        GroovyBridge.attach(target)
    }
}
/**
 * Provides the existing `antiAlias`: [AntiAlias] task.
 */
val TaskContainer.`antiAlias`: TaskProvider<AntiAlias>
    get() = runCatching {
        named<AntiAlias>(R.task.antiAlias)
    }.getOrElse {
        register<AntiAlias>(R.task.antiAlias)
    }
/**
 * Provides the existing `genModHjson`: [ModHjsonGenerate] task.
 */
val TaskContainer.`genModHjson`: TaskProvider<ModHjsonGenerate>
    get() = named<ModHjsonGenerate>(R.task.genModHjson)

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
    get() = named<ResolveMods>(R.task.resolveMods)

