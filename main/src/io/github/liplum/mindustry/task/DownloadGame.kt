package io.github.liplum.mindustry.task

import io.github.liplum.dsl.boolProp
import io.github.liplum.dsl.copyTo
import io.github.liplum.dsl.ensure
import io.github.liplum.dsl.prop
import io.github.liplum.mindustry.GitHubGameLoc
import io.github.liplum.mindustry.IGameLoc
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class DownloadGame : DefaultTask() {
    val location = project.prop<IGameLoc>()
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
            logger.info("$gameLoc has been already downloaded at ${output.absolutePath} , so skip it.")
        }
    }

    private fun useCacheOrDownload(gameLoc: IGameLoc) {
        // Download is a very expensive task, it should detect whether the file exists.
        if (gameLoc is GitHubGameLoc) {
            val downloadLoc = gameLoc.createDownloadLoc()
            val userHome = System.getProperty("user.home")
            val cacheFile = File(userHome).resolve(".gradle").resolve("mindustry")
                .resolve("github").resolve(gameLoc.fileName).ensure()
            if (!cacheFile.exists()) {
                logger.lifecycle("Downloading $downloadLoc from ${gameLoc.fileName} .")
                downloadLoc.openInputStream().use { it.copyTo(cacheFile) }
                logger.info("Downloaded ${gameLoc.fileName} at ${cacheFile}.")
            }
            if (!outputFile.exists()) {
                cacheFile.copyTo(outputFile)
            }
        } else {
            if (!outputFile.exists()) {
                val downloadLoc = gameLoc.createDownloadLoc()
                logger.lifecycle("Downloading $downloadLoc from ${gameLoc.fileName} .")
                downloadLoc.openInputStream().use { it.copyTo(outputFile) }
                logger.info("Downloaded ${gameLoc.fileName} at ${outputFile}.")
            }
        }
    }
}
