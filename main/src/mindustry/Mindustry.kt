@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.*
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
 * The main extension of [MindustryPlugin].
 * It provides many configurations for Mindustry modding development:
 * - [meta]: the `mod.(h)json` that will be included in the `:jar` task.
 * You can modify this, but it only affects the output file.
 */
open class MindustryExtension(
    target: Project,
) {
    /**
     * The check time(sec) for latest version.
     *
     * 1 hour by default.
     */
    var outOfDateTime: Int
        get() = (R.outOfDataDuration / 1000).toInt()
        set(value) {
            R.outOfDataDuration = value * 1000L
        }

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
                findFileInOrder(
                    project.layout.projectDirectory.file("mod.hjson").asFile,
                    project.layout.projectDirectory.file("mod.json").asFile,
                    project.rootProject.layout.projectDirectory.file("mod.hjson").asFile,
                    project.rootProject.layout.projectDirectory.file("mod.json").asFile,
                )?.let {
                    ModMeta.fromHjson(it)
                }
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
        get() = if (_modMeta.isPresent) _modMeta.get()
        else ModMeta().apply {
            _modMeta.set(this)
        }
        set(value) = _modMeta.set(value)

    fun modMeta(config: ModMeta.() -> Unit) {
        meta.config()
    }

    fun modMeta(config: Action<ModMeta>) {
        config.execute(meta)
    }
}