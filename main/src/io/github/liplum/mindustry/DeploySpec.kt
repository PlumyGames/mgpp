package io.github.liplum.mindustry

import io.github.liplum.dsl.prop
import io.github.liplum.dsl.stringProp
import org.gradle.api.Project

/**
 * You can configure the process of a jar compatible with both Desktop and Android.
 */
@DisableIfWithout("java")
class DeploySpec(
    target: Project,
) {
    @JvmField
    val _baseName = target.stringProp().apply {
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
    val _version = target.stringProp().apply {
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
    val _classifier = target.stringProp().apply {
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
    @InheritFromParent
    val _androidSdkRoot = target.stringProp().apply {
        convention(System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT") ?: "")
    }
    /**
     * Configure the path of Android SDK.
     *
     * It will check environment variable `ANDROID_HOME` or `ANDROID_SDK_ROOT` as default.
     */
    @InheritFromParent
    var androidSdkRoot: String
        get() = _androidSdkRoot.getOrElse("")
        set(value) {
            _androidSdkRoot.set(value)
        }
    /**
     * Whether to make a fat jar, which contains all dependencies from classpath.
     *
     * Basically, `enabled` as default.
     */
    @PropertyAsDefault("mgpp.deploy.enableFatJar", "true")
    val enableFatJar = target.prop<Boolean>().apply {
        val enableFatJarGlobal =
            !target.properties.getOrDefault("mgpp.deploy.enableFatJar", "true").toString()
                .equals("false", ignoreCase = true)
        convention(enableFatJarGlobal)
    }
    /**
     * Enable the fat jar.
     *
     * `enable` as default
     * @see [enableFatJar]
     */
    val fatJar: Unit
        get() {
            enableFatJar.set(true)
        }
    /**
     * Disable the fat jar.
     *
     * `disable` as default
     * @see [enableFatJar]
     */
    val noFatJar: Unit
        get() {
            enableFatJar.set(false)
        }
}