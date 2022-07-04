package io.github.liplum.mindustry

import io.github.liplum.dsl.stringProp
import org.gradle.api.Project
import io.github.liplum.mindustry.task.RunMindustry

/**
 * You can configure how to dispose of the data Mindustry generated during running.
 */
class RunSpec(
    target: Project,
) {
    val _dataDir = target.stringProp().apply {
        convention("temp")
    }
    /**
     * The data directory for Mindustry running.
     *
     * It will be set to the [RunMindustry.getTemporaryDir] as default
     */
    var dataDir: String
        get() = _dataDir.getOrElse("temp")
        set(value) {
            _dataDir.set(value)
        }
    /**
     * Set the [dataDir] to the default path of which Mindustry commonly used
     * @see [resolveDefaultDataDir]
     */
    fun setDataDefault() {
        _dataDir.set("")
    }
    /**
     * Set the [dataDir] to the [RunMindustry.getTemporaryDir].
     */
    fun setDataTemp() {
        _dataDir.set("temp")
    }
}