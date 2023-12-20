package io.github.liplum.mindustry

import io.github.liplum.dsl.FileAt
import io.github.liplum.dsl.OS
import io.github.liplum.dsl.getOs
import io.github.liplum.mindustry.GameSideType.*
import org.gradle.api.Task
import java.io.File
import java.io.Serializable

sealed interface IDataDirLoc : Serializable {
    fun resolveDir(task: Task, type: GameSideType): File?
}

data class LocalDataDirLoc(
    val dir: File
) : IDataDirLoc {
    override fun resolveDir(task: Task, type: GameSideType): File = dir
}

data class ProjBuildDataDirLoc(
    val namespace: String,
    val name: String,
) : IDataDirLoc {
    override fun resolveDir(task: Task, type: GameSideType): File {
        return task.project.layout.buildDirectory.asFile.get().resolve(namespace).resolve(name)
    }
}

object MindustryDefaultDataDirLoc : IDataDirLoc {
    private fun readResolve(): Any = MindustryDefaultDataDirLoc

    /**
     * Returns null if [type] is [GameSideType.Server].
     */
    override fun resolveDir(task: Task, type: GameSideType): File? {
        return when (type) {
            Client -> resolveDefaultClientDataDir()
            Server -> null
        }
    }
}

internal
fun resolveDefaultClientDataDir(): File? {
    return when (getOs()) {
        OS.Unknown -> null
        OS.Windows -> FileAt(System.getenv("AppData"), "Mindustry")
        OS.Linux -> FileAt(System.getenv("XDG_DATA_HOME") ?: System.getenv("HOME"), ".local", "share", "Mindustry")
        OS.Mac -> FileAt(System.getenv("HOME"), "Library", "Application Support", "Mindustry")
    }
}