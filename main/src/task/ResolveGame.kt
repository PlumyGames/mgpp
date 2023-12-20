package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.dsl.fileProp
import io.github.liplum.dsl.prop
import io.github.liplum.mindustry.SharedCache.isUpdateToDate
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
        cacheFile.ensureParentDir()
        when (loc) {
            is LocalGameLoc -> if (!cacheFile.isFile) throw GradleException("Local game $cacheFile doesn't exists.")
            is ILatestDownloadableGameLoc -> if (!isUpdateToDate(lockFile = cacheFile)) loc.download(cacheFile)
            is IDownloadableGameLoc -> if (!cacheFile.exists()) loc.download(cacheFile)
            else -> throw Exception("Unhandled game loc $loc")
        }
        if (cacheFile.exists()) {
            createSymbolicLinkOrCopy(link = gameFile, target = cacheFile)
        } else {
            logger.error("$cacheFile doesn't exist.")
        }
    }

    fun IDownloadableGameLoc.download(cacheFile: File) {
        logger.lifecycle("Downloading $this -> $cacheFile...")
        try {
            this.resolveDownloadSrc().openStream().copyToTmpAndMove(cacheFile)
            logger.info("$cacheFile was downloaded.")
        } catch (e: Exception) {
            logger.error("Failed to download $this", e)
        }
    }
}