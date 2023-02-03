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
     * `enabled` as default.
     */
    @InheritFromParent
    val enableFatJar = target.prop<Boolean>().apply {
        convention(true)
    }
    /**
     * Enable the fat jar.
     *
     * `fatJar` as default
     * @see [enableFatJar]
     */
    val fatJar: Unit
        get() {
            enableFatJar.set(true)
        }
    /**
     * Disable the fat jar.
     *
     * `fatJar` as default
     * @see [enableFatJar]
     */
    val noFatJar: Unit
        get() {
            enableFatJar.set(false)
        }
}