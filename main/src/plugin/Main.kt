@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider

class MindustryPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        GroovyBridge.attach(target)
        // Register this for dynamically configure tasks without class reference in groovy.
        if (plugins.hasPlugin<JavaPlugin>()) {
            plugins.apply<MindustryJavaPlugin>()
        } else {
            plugins.apply<MindustryJsonPlugin>()
        }
        plugins.apply<MindustryRunPlugin>()
        val ex = extensions.getOrCreate<MindustryExtension>(R.x.mindustry)
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
        target.tasks.register<CleanMindustrySharedCache>(R.task.cleanMindustrySharedCache) {
            group = BasePlugin.BUILD_GROUP
        }

        target.afterEvaluateThis {
            if (ex._modMeta.isPresent) {
                tasks.register<ModHjsonGenerate>(R.task.genModHjson) {
                    group = R.taskGroup.mindustry
                    modMeta.set(ex._modMeta)
                    output.set(temporaryDir.resolve("mod.hjson"))
                }

            }
        }
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

