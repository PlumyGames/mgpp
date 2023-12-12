package io.github.liplum.mindustry

import io.github.liplum.dsl.copyTo
import io.github.liplum.dsl.fileProp
import io.github.liplum.dsl.listProp
import io.github.liplum.dsl.prop
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Files


open class ResolveGame : DefaultTask() {
    val location = project.prop<IGameLoc>()
        @Input get
    val mods = project.listProp<IMod>()
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
                is GitHubGameLoc -> loc.downloadTo(cacheFile)
                is LocalGameLoc -> loc.downloadTo(cacheFile)
            }
        }
        createSymbolicLinkOrCopyCache(gameFile, cacheFile)
    }

    fun createSymbolicLinkOrCopyCache(gameFile: File, cacheFile: File) {
        if (!gameFile.exists()) return
        try {
            Files.createSymbolicLink(gameFile.toPath(), cacheFile.toPath())
            logger.lifecycle("Created symbolic link of game: $cacheFile -> $gameFile.")
        } catch (error: Exception) {
            logger.lifecycle("Cannot create symbolic link of game: $cacheFile -> $gameFile, because $error.")
            logger.lifecycle("Fallback to copy file.")
            cacheFile.copyTo(gameFile)
            logger.lifecycle("Game was copied: $cacheFile -> $gameFile.")
        }
    }

    fun GitHubGameLoc.downloadTo(cacheFile: File) {
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

    fun LocalGameLoc.downloadTo(cacheFile: File) {
        if (!cacheFile.isFile) {
            throw GradleException("Local game $cacheFile doesn't exists.")
        }
    }
}