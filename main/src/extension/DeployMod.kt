@file:JvmMultifileClass
@file:JvmName("ExtensionKt")
@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.boolProp
import io.github.liplum.dsl.prop
import io.github.liplum.dsl.stringProp
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

/**
 * Retrieves the `runMindustry`: [DeployModExtension] extension.
 */
val Project.`deployMod`: DeployModExtension
    get() = (this as ExtensionAware).extensions.getByName(R.x.deployMod) as DeployModExtension
/**
 * Configures the [mindustry][DeployModExtension] extension.
 */
fun Project.`deployMod`(configure: Action<DeployModExtension>): Unit =
    (this as ExtensionAware).extensions.configure(R.x.deployMod, configure)
/**
 * [runMindustry] is used to create `runClient` and `runServer` tasks dynamically after build script is evaluated.
 *
 * Therefore, you can simply ignore `runMindustry` if you don't want to run the game.
 */
open class DeployModExtension(
    val proj: Project,
) {
    @JvmField
    val _baseName = proj.stringProp().apply {
        convention("")
    }
    /**
     * The deploy jar name: "[baseName]-[version]-[classifier].jar".
     *
     * [ModMeta.name] in [mindustry] as default
     */
    var baseName: String
        get() = _baseName.getOrElse("")
        set(value) {
            _baseName.set(value)
        }
    @JvmField
    val _version = proj.stringProp().apply {
        convention("")
    }
    /**
     * The deploy jar name: "[baseName]-[version]-[classifier].jar"
     *
     * [ModMeta.version] in [mindustry] as default
     */
    var version: String
        get() = _version.getOrElse("")
        set(value) {
            _version.set(value)
        }
    @JvmField
    val _classifier = proj.stringProp().apply {
        convention("")
    }
    /**
     * The deploy jar name: "[baseName]-[version]-[classifier].jar"
     *
     * An empty string as default
     */
    var classifier: String
        get() = _classifier.getOrElse("")
        set(value) {
            _classifier.set(value)
        }

    val _enableFatJar = proj.prop<Boolean>().apply {
        convention(true)
    }
    /**
     * Whether to make a fat jar, which contains all dependencies from classpath.
     * `true` as default.
     *
     * If current [proj] is a subproject, it'll be `false` as default,
     * which avoids all subprojects to output a fat jar.
     *
     * Therefore, you should manually set this to `true` when working with multi-project.
     */
    var enableFatJar: Boolean
        get() = _enableFatJar.getOrElse(true)
        set(value) {
            _enableFatJar.set(value)
        }
    @JvmField
    val _outputMod = proj.boolProp().apply {
        convention(true)
    }
    /**
     * Whether this project could output a mod file in `:jar` task,
     * its jar will contain something a mod needs. such as `mod.hjson`.
     *
     * If current [proj] is a subproject, it'll be `false` as default,
     * which avoids all subprojects to output a mod file.
     *
     * Therefore, you should manually set this to `true` when working with multi-project.
     */
    var outputMod: Boolean
        get() = _outputMod.getOrElse(true)
        set(value) = _outputMod.set(value)
}