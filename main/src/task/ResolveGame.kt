package io.github.liplum.mindustry

import io.github.liplum.dsl.copyTo
import io.github.liplum.dsl.createSymbolicLinkOrCopyCache
import io.github.liplum.dsl.fileProp
import io.github.liplum.dsl.prop
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File


open class ResolveGame : DefaultTask() {
    val location = project.prop<IGameLoc>()
        @Input get
    val gameFile = project.fileProp()
        @OutputFile get

    init {
        gameFile.convention(project.provider {
            temporaryDir.resolve(location.get().fileName4Local)
        })
        outputs.upToDateWhen {
            gameFile.get().exists()
        }
    }

    @TaskAction
    fun resolve() {
        val gameFile = gameFile.get()
        val loc = location.get()
        val cacheFile = loc.resolveCacheFile()
        if (!cacheFile.exists()) {
            when (loc) {
                is LocalGameLoc -> if (!cacheFile.isFile) throw GradleException("Local game $cacheFile doesn't exists.")
                else -> loc.download(cacheFile)
            }
        }
        createSymbolicLinkOrCopyCache(link = gameFile, target = cacheFile)
    }

    fun IGameLoc.download(cacheFile: File) {
        logger.lifecycle("Downloading $this -> $cacheFile...")
        try {
            this.createDownloadLoc().openInputStream().use {
                it.copyTo(cacheFile)
            }
            logger.lifecycle("${this.fileName4Local} was downloaded.")
        } catch (e: Exception) {
            // now output is corrupted, delete it
            cacheFile.delete()
            throw e
        }
    }
}