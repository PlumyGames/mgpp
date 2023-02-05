package io.github.liplum.mindustry


import io.github.liplum.dsl.*
import mindustry.task.PackModZip
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * For json & javascript mod development.
 */
class MindustryJsonPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        val assets = extensions.getOrCreate<MindustryAssetsExtension>(R.x.mindustryAssets)
        val deployX = extensions.getOrCreate<DeployModExtension>(R.x.deployMod)
        tasks.register<PackModZip>(R.task.packModZip) {
            this.group = R.taskGroup.mindustry
            from(assets.assetsRoot)
            from(assets._icon)
            from(tasks.getByPath(R.task.genModHjson))
            archiveBaseName.set(deployX._baseName)
            archiveVersion.set(deployX._version)
            archiveClassifier.set(deployX._classifier)
            destinationDirectory.set(layout.buildDirectory.dir("libs"))
        }
    }
}