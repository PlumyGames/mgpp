@file:JvmMultifileClass
@file:JvmName("ExtensionKt")
@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.mindustry.*
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
     * The assets root of a mod only including a single `assets` folder
     */
    @JvmField
    val assetsRoot = proj.fileProp().apply {
        convention(proj.projectDir.resolve("assets"))
    }
    /**
     * Set the [assetsRoot] to [path]
     */
    fun rootAt(path: String) {
        assetsRoot.set(File(path))
    }
    /**
     * Set the [assetsRoot] to [file]
     */
    fun rootAt(file: File) {
        assetsRoot.set(file)
    }

    /**
     * The icon of this mod to be included in `:jar` task.
     *
     * [Project.getRootDir]/icon.png as default
     */
    @JvmField
    val _icon = proj.fileProp().apply {
        convention(
            findFileInOrder(
                proj.projDir("icon.png"),
                proj.rootDir("icon.png")
            )
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
    /**
     * ### Kotlin DSL
     * A spec for configuring [assetsRoot].
     */
    val root = AssetRootSpec()
    /**
     * ### Kotlin DSL
     * A spec for configuring [icon].
     */
    val icon = IconSpec()

    /**
     * For configuring [assetsRoot]
     */
    inner class AssetRootSpec {
        /**
         * Set [assetsRoot] to [folder]
         */
        infix fun at(folder: File) {
            assetsRoot.set(folder)
        }
        /**
         * Set [assetsRoot] to [path]
         */
        infix fun at(path: String) {
            assetsRoot.set(File(path))
        }
    }

    inner class IconSpec {
        /**
         * Set [assetsRoot] to [file]
         */
        infix fun at(file: File) {
            _icon.set(file)
        }
        /**
         * Set [_icon] to [path]
         */
        infix fun at(path: String) {
            _icon.set(File(path))
        }
    }
}
