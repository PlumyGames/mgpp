package plumy.mindustry.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import plumy.dsl.listProp
import plumy.mindustry.IMod
import java.io.File

open class ResolveModsTask : DefaultTask() {
    val mods = project.listProp<IMod>()
        @Input get
    val downloadedMos: List<File>
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