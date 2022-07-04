package io.github.liplum.mindustry

import io.github.liplum.dsl.stringProp
import org.gradle.api.Project

/**
 * You can configure how to dispose of the data Mindustry generated during running.
 */
class RunSpec(
    target: Project,
) {
    val _dataDir = target.stringProp().apply {
        convention("temp")
    }
    var dataDir: String
        get() = _dataDir.getOrElse("")
        set(value) {
            _dataDir.set(value)
        }

    fun setDataDefault() {
        _dataDir.set("")
    }

    fun setDataTemp() {
        _dataDir.set("temp")
    }
}