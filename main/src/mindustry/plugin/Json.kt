package io.github.liplum.mindustry


import io.github.liplum.dsl.*
import io.github.liplum.mindustry.*
import io.github.liplum.mindustry.task.DexJar
import mindustry.task.PackModZip
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import java.io.File

/**
 * For json & javascript mod development.
 */
class MindustryJsonPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        val assets = extensions.getOrCreate<MindustryAssetsExtension>(
            R.x.mindustryAssets
        )
        val packModZip = tasks.register<PackModZip>(R.task.packModZip) {
            this.group = R.taskGroup.mindustry
            from(assets.assetsRoot)
            from(assets._icon)
            from(tasks.getByPath(R.task.genModHjson))
            archiveFileName.set("Mod.zip")
            destinationDirectory.set(layout.buildDirectory.dir("libs"))
        }
        afterEvaluateThis {

        }
    }
}