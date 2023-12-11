package io.github.liplum.mindustry.task

import io.github.liplum.dsl.boolProp
import io.github.liplum.dsl.copyTo
import io.github.liplum.dsl.ensure
import io.github.liplum.dsl.prop
import io.github.liplum.mindustry.GitHubGameLoc
import io.github.liplum.mindustry.IGameLoc
import io.github.liplum.mindustry.SharedCache
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files

open class DownloadGame : DefaultTask() {
    val location = project.prop<IGameLoc<*>>()
        @Input get
    val overwrite = project.boolProp()
        @Optional @Input get
    val keepOthers = project.boolProp()
        @Optional @Input get
    val outputFile
        @OutputFile get() = temporaryDir.resolve(location.get().fileName)

    init {
        overwrite.convention(false)
        keepOthers.convention(true)
        outputs.upToDateWhen {
            outputFile.exists()
        }
    }

    @TaskAction
    fun download() {
        val output = outputFile
        if (!keepOthers.get()) {
            ((temporaryDir.listFiles() ?: emptyArray()).toList() - output).forEach {
                it.delete()
            }
        }
        val gameLoc = location.get()
        if (!output.exists() || overwrite.get()) {
            useCacheOrDownload(gameLoc)
        } else {
            logger.info("$gameLoc has been already downloaded at ${output.absolutePath}, so skip it.")
        }
    }

    private fun useCacheOrDownload(gameLoc: IGameLoc<*>) {
        // Download is a very expensive task, it should detect whether the file exists.
        if (gameLoc is GitHubGameLoc) {
            val downloadLoc = gameLoc.createDownloadLoc()
            val cacheFile = SharedCache.gamesDir.resolve("github").resolve(gameLoc.fileName).ensure()
            if (!cacheFile.exists()) {
                logger.lifecycle("Downloading $downloadLoc...")
                try {
                    downloadLoc.openInputStream().use { it.copyTo(cacheFile) }
                    logger.lifecycle("Downloaded ${gameLoc.fileName} at ${cacheFile}.")
                } catch (e: Exception) {
                    // now cache could be corrupted, delete it
                    cacheFile.delete()
                    throw e
                }
            }
            if (!outputFile.exists()) {
                try {
                    Files.createSymbolicLink(outputFile.toPath(),cacheFile.toPath())
                    logger.lifecycle("Created symbolic link of game: $cacheFile -> $outputFile.")
                } catch (error: Exception) {
                    logger.warn("Cannot create symbolic link of game: $cacheFile -> $outputFile, because $error.")
                    logger.warn("Fallback to copy file.")
                    cacheFile.copyTo(outputFile)
                    logger.lifecycle("Game was copied: $cacheFile -> $outputFile.")
                }
            }
        } else {
            if (!outputFile.exists()) {
                val downloadLoc = gameLoc.createDownloadLoc()
                logger.lifecycle("Downloading $downloadLoc from ${gameLoc.fileName} .")
                downloadLoc.openInputStream().use { it.copyTo(outputFile) }
                logger.lifecycle("Downloaded ${gameLoc.fileName} at ${outputFile}.")
            }
        }
    }
}
