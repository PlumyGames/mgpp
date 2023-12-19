package io.github.liplum.mindustry

import org.gradle.api.Task
import java.io.File
import java.io.Serializable

sealed interface IDataDirLoc : Serializable {
    fun resolveDir(task: Task): File?
}

data class LocalDataDirLoc(
    val dir: File
) : IDataDirLoc {
    override fun resolveDir(task: Task): File = dir
}

data class ProjBuildDataDirLoc(
    val namespace: String,
    val name: String,
) : IDataDirLoc {
    override fun resolveDir(task: Task): File {
        return task.project.layout.buildDirectory.asFile.get().resolve(namespace).resolve(name)
    }
}