package io.github.liplum.mindustry.task

import io.github.liplum.dsl.listProp
import io.github.liplum.mindustry.IMod
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ResolveMods : DefaultTask() {
    val mods = project.listProp<IMod>()
        @Input get
    val downloadedMods: List<File>
        @OutputFiles get() = mods.get().mapNotNull {
            it.mapLocalFile(project, temporaryDir).let { fi ->
                if (fi.exists()) fi else null
            }
        }
    @TaskAction
    fun resolve() {
        mods.get().forEach {
            try {
                val resolved = it.resolveFile(project, temporaryDir)
                logger.info("resolved $it into ${resolved.absolutePath} .")
            } catch (e: Exception) {
                logger.warn("Can't resolve the $it", e)
            }
        }
    }
}