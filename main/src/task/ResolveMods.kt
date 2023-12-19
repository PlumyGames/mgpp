package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.dsl.listProp
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

internal
const val infoX = "info.json"

open class ResolveMods : DefaultTask() {
    val mods = project.listProp<IMod>()
        @Input get
    val downloadedMods = project.listProp<File>()
        @OutputFiles get

    init {
        downloadedMods.convention(
            project.provider {
                mods.get().map {
                    it.resolveOutputFile()
                }
            }
        )
    }

    fun IMod.resolveOutputFile(): File {
        val namespace = when (this) {
            is IGitHubMod -> "github"
            is LocalMod -> "local"
            is UrlMod -> "url"
            else -> "other"
        }
        return temporaryDir.resolve(namespace).resolve(this.fileName4Local)
    }

    @TaskAction
    fun resolve() {
        for (mod in mods.get()) {
            val cacheFile = mod.resolveCacheFile()
            if (!cacheFile.exists()) {
                when (mod) {
                    is LocalMod -> if (!cacheFile.isFile) throw GradleException("Local mod $cacheFile not found.")
                    is IGitHubMod -> {
                        if (!mod.isUpdateToDate()) {
                            logger.lifecycle("Updating $this -> $cacheFile...")
                            try {
                                mod.resolveDownloadSrc().openStream().copyToTmpAndMove(cacheFile)
                                logger.info("$cacheFile was downloaded.")
                            } catch (e: Exception) {
                                logger.error("Failed to update $this", e)
                            }
                        }
                    }

                    is IDownloadableMod -> {
                        logger.lifecycle("Downloading $this -> $cacheFile...")
                        try {
                            mod.resolveDownloadSrc().openStream().copyToTmpAndMove(cacheFile)
                            logger.info("$cacheFile was downloaded.")
                        } catch (e: Exception) {
                            logger.error("Failed to download $this", e)
                        }
                    }

                    else -> {}
                }
            }
            createSymbolicLinkOrCopyCache(link = mod.resolveOutputFile(), target = cacheFile)
        }
    }
}

