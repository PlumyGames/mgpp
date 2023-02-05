@file:JvmMultifileClass
@file:JvmName("ExtensionKt")
@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.mindustry.*
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

/**
 * Retrieves the [mindustry][MindustryExtension] extension.
 */
val Project.`mindustry`: MindustryExtension
    get() = (this as ExtensionAware).extensions.getByName(R.x.mindustry) as MindustryExtension
/**
 * Configures the [mindustry][MindustryExtension] extension.
 */
fun Project.`mindustry`(configure: Action<MindustryExtension>): Unit =
    (this as ExtensionAware).extensions.configure(R.x.mindustry, configure)
/**
 * The project type.
 */
enum class ProjectType {
    /**
     * For a mod, it will import all essential dependencies of Mindustry.
     */
    Mod,
    /**
     * For a plugin, it will only import server-related dependencies of Mindustry.
     */
    Plugin
}
/**
 * The main extension of [MindustryPlugin].
 * It provides many configurations for Mindustry modding development:
 * - [meta]: the `mod.(h)json` that will be included in the `:jar` task.
 * You can modify this, but it only affects the output file.
 */
open class MindustryExtension(
    target: Project,
) {
    /**
     * @see ProjectType.Mod
     */
    @JvmField
    val Mod = ProjectType.Mod
    /**
     * @see ProjectType.Plugin
     */
    @JvmField
    val Plugin = ProjectType.Plugin
    /**
     * The check time(sec) for latest version.
     *
     * 1 hour as default.
     */
    var outOfDateTime: Int
        get() = (R.outOfDataTime / 1000).toInt()
        set(value) {
            R.outOfDataTime = value * 1000L
        }
    val _projectType = target.prop<ProjectType>().apply {
        convention(ProjectType.Mod)
    }
    /**
     * The project type will influence dependency resolution.
     */
    var projectType: ProjectType
        get() = _projectType.getOrElse(ProjectType.Mod)
        set(value) = _projectType.set(value)
    @JvmField
    val _dependency = DependencySpec(target)
    /**
     * Configure the mindustry and arc dependency.
     * You should call [mindustryRepo] and [importMindustry] to apply the configuration.
     */
    fun dependency(func: Action<DependencySpec>) {
        func.execute(_dependency)
    }
    /**
     * Configure the mindustry and arc dependency.
     * You should call [mindustryRepo] and [importMindustry] to apply the configuration.
     */
    inline fun dependency(func: DependencySpec.() -> Unit) {
        _dependency.func()
    }
    @JvmField
    val _modMeta = target.prop<ModMeta>().apply {
        target.run {
            convention(
                ModMeta.fromHjson(
                    findFileInOrder(
                        projDir("mod.hjson"),
                        projDir("mod.json"),
                        rootDir("mod.hjson"),
                        rootDir("mod.json")
                    )
                )
            )
        }
    }
    /**
     * Configure `mod.hjson` for output purpose.
     * It will automatically fetch the `mod.(h)json` from the following paths in order:
     * 1. [Project.getProjectDir]/mod.hjson
     * 2. [Project.getProjectDir]/mod.json
     * 3. [Project.getRootProject]/mod.hjson
     * 4. [Project.getRootProject]/mod.json
     */
    var meta: ModMeta
        get() = _modMeta.get()
        set(value) = _modMeta.set(value)

    fun modMeta(config: ModMeta.() -> Unit) {
        meta.config()
    }

    fun modMeta(config: Action<ModMeta>) {
        config.execute(meta)
    }
}