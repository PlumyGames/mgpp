@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.mindustry.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.jvm.tasks.Jar


class MindustryAssetPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        val main = extensions.getOrCreate<MindustryExtension>(
            R.x.mindustry
        )
        val assets = extensions.getOrCreate<MindustryAssetsExtension>(
            R.x.mindustryAssets
        )
        val deployX = extensions.getOrCreate<DeployModExtension>(R.x.deployMod)
        afterEvaluateThis {
            tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
                from(assets.assetsRoot)
            }
            // TODO: Redesign this
            if (deployX.outputMod) {
                tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
                    from(assets._icon)
                }
                tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
                    dependsOn(R.task.genModHjson)
                    from(tasks.getByPath(R.task.genModHjson))
                }
            }
        }
    }
}