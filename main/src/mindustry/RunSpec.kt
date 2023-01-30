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
    @DefaultValue("<temp>")
    @LocalProperty("mgpp.run.dataDir")
    val _dataDir = target.stringProp().apply {
        convention("<temp>")
    }
    /**
     * The data directory for Mindustry running.
     *
     * It will be set to the [RunMindustry.getTemporaryDir] as default.
     *
     * `mgpp.run.dataDir` in `local.properties` will overwrite this.
     */
    @InheritFromParent
    @DefaultValue("temp")
    @LocalProperty("mgpp.run.dataDir")
    var dataDir: String
        get() = _dataDir.getOrElse("<temp>")
        set(value) {
            _dataDir.set(value)
        }
    /**
     * Whether to delete all mods other than any in [ModsSpec.worksWith] in the data directory,
     * ensure you have backed up your mods.
     *
     * `mgpp.run.forciblyClear` in `local.properties` will overwrite this.
     */
    @InheritFromParent
    @DefaultValue("false")
    @LocalProperty("mgpp.run.forciblyClear")
    val _forciblyClear = target.boolProp().apply {
        convention(false)
    }
    /**
     * Whether to delete all mods other than any from [ModsSpec.worksWith] in the data directory,
     * ensure you have backed up your mods.
     */
    @InheritFromParent
    @DefaultValue("false")
    @LocalProperty("mgpp.run.forciblyClear")
    var forciblyClear: Boolean
        get() = _forciblyClear.getOrElse(false)
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
            _dataDir.set("<default>")
        }
    /**
     * Set the [dataDir] to the [RunMindustry.getTemporaryDir].
     */
    val useTempDataDir: Unit
        get() {
            _dataDir.set("<temp>")
        }
    /**
     * Set the [dataDir] to the environment variable,
     */
    val useEnvDataDir: Unit
        get() {
            _dataDir.set("<env>")
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
        _dataDir.set("<default>")
    }
    /**
     * Set the [dataDir] to the [RunMindustry.getTemporaryDir].
     */
    fun setDataTemp() {
        _dataDir.set("<temp>")
    }
}