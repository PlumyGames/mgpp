package io.github.liplum.mindustry.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import io.github.liplum.dsl.*
import io.github.liplum.mindustry.*
import java.io.File

open class ResolveMods : DefaultTask() {
    val mods = project.listProp<IMod>()
        @Input get
    val downloadedMods: List<File>
        @OutputFiles get() = mods.get().map { it.mapLocalFile(temporaryDir) }
    @TaskAction
    fun resolve() {
        mods.get().forEach {
            try {
                it.resolveFile(temporaryDir)
            } catch (e: Exception) {
                logger.warn("Can't resolve the $it", e)
            }
        }
    }
}