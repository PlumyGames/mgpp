package io.github.liplum.mindustry


import io.github.liplum.dsl.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip

/**
 * For json & javascript mod development.
 */
class MindustryJsonPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        val x = extensions.getOrCreate<MindustryExtension>(R.x.mindustry)
        val assets = extensions.getOrCreate<MindustryAssetsExtension>(R.x.mindustryAssets)
        val deployX = extensions.getOrCreate<DeployModExtension>(R.x.deployMod)
        tasks.register<Zip>(R.task.zipMod) {
            this.group = R.taskGroup.mindustry
            from(assets.assetsRoot)
            from(assets._icon)
            from(tasks.getByPath(R.task.genModHjson))
            archiveBaseName.set(deployX._baseName)
            archiveVersion.set(deployX._version)
            archiveClassifier.set(deployX._classifier)
            destinationDirectory.set(layout.buildDirectory.dir("libs"))
        }
        x.modMeta {
            // json or js mod doesn't have a main class
            main = null
            java = false
        }
    }
}
/**
 * Provides the existing [zipMod][Zip] task.
 */
val TaskContainer.`zipMod`: TaskProvider<Zip>
    get() = named<Zip>(R.task.zipMod)