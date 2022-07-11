package io.github.liplum.mindustry

import io.github.liplum.dsl.boolProp
import io.github.liplum.dsl.stringProp
import io.github.liplum.mindustry.task.RunMindustry
import org.gradle.api.Project

/**
 * You can configure how to dispose of the data Mindustry generated during running.
 */
class RunSpec(
    target: Project,
) {
    @InheritFromParent
    @DefaultValue("temp")
    val _dataDir = target.stringProp().apply {
        convention("temp")
    }
    /**
     * The data directory for Mindustry running.
     *
     * It will be set to the [RunMindustry.getTemporaryDir] as default
     */
    @InheritFromParent
    @DefaultValue("temp")
    var dataDir: String
        get() = _dataDir.getOrElse("temp")
        set(value) {
            _dataDir.set(value)
        }
    /**
     * Whether to delete all mods other than any in [ModsSpec.worksWith] in the data directory,
     * ensure you have backed up your mods.
     */
    @DefaultValue("true")
    @InheritFromParent
    val _forciblyClear = target.boolProp().apply {
        convention(true)
    }
    /**
     * Whether to delete all mods other than any in [ModsSpec.worksWith] in the data directory,
     * ensure you have backed up your mods.
     */
    @DefaultValue("true")
    var forciblyClear: Boolean
        get() = _forciblyClear.getOrElse(true)
        set(value) {
            _forciblyClear.set(value)
        }
    /**
     * Clear all things in the data directory
     */
    val clearOtherMods: Unit
        get() {
            _forciblyClear.set(true)
        }
    /**
     * Keep anything in the data directory
     */
    val keepOtherMods: Unit
        get() {
            _forciblyClear.set(false)
        }
    /**
     * Set the [dataDir] to the default path of which Mindustry commonly used.
     * - **Linux**: `$HOME/.local/share/Mindustry/`
     * - **MacOS**: `$HOME/Library/Application Support/Mindustry/`
     * - **Windows**:`%AppData%/Mindustry/`
     * @see [resolveDefaultDataDir]
     */
    val useDefaultDataDir: Unit
        get() {
            _dataDir.set("")
        }
    /**
     * Set the [dataDir] to the [RunMindustry.getTemporaryDir].
     */
    val useTempDataDir: Unit
        get() {
            _dataDir.set("temp")
        }
    /**
     * Set the [dataDir] to the default path of which Mindustry commonly used
     * - **Linux**: `$HOME/.local/share/Mindustry/`
     * - **MacOS**: `$HOME/Library/Application Support/Mindustry/`
     * - **Windows**:`%AppData%/Mindustry/`
     *
     * **NOTE** It will delete all mods other than any in [ModsSpec.worksWith] in this folder, ensure you have backed up your mods.
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