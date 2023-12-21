@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import java.io.File

/**
 * Retrieves the [mindustry][MindustryExtension] extension.
 */
val Project.`mindustryAssets`: MindustryAssetsExtension
    get() = (this as ExtensionAware).extensions.getByName(R.x.mindustryAssets) as MindustryAssetsExtension

/**
 * Configures the [mindustry][MindustryExtension] extension.
 */
fun Project.`mindustryAssets`(configure: Action<MindustryAssetsExtension>): Unit =
    (this as ExtensionAware).extensions.configure(R.x.mindustryAssets, configure)

open class MindustryAssetsExtension(
    proj: Project,
) {
    /**
     * The assets of a mod.
     * It includes the `assets/` folder under current project by default.
     */
    val assets = proj.configurationFileCollection().apply {
        from(proj.projectDir.resolve("assets"))
    }

    /**
     * The icon of this mod to be included in `:jar` task.
     *
     * [Project.getRootDir]/icon.png by default
     */
    @JvmField
    val _icon = proj.fileProp().apply {
        val inProj = proj.layout.projectDirectory.file("icon.png").asFile
        val inRoot = proj.rootProject.layout.projectDirectory.file("icon.png").asFile
        convention(
            findFileInOrder(
                inProj,
                inRoot,
            ) ?: inRoot
        )
    }

    /**
     * Set the [_icon] to [file]
     */
    fun iconAt(file: File) {
        _icon.set(file)
    }

    /**
     * Set the [_icon] to [path]
     */
    fun iconAt(path: String) {
        _icon.set(File(path))
    }
}
