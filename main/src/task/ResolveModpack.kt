package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.dsl.listProp
import io.github.liplum.mindustry.SharedCache.checkUpdateToDate
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

private
const val lockFile = "lock.json"

open class ResolveModpack : DefaultTask() {
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
        outputs.upToDateWhen {
            false
        }
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
            cacheFile.ensureParentDir()
            when (mod) {
                is LocalMod -> if (!cacheFile.isFile) {
                    throw GradleException("Local mod $cacheFile not found.")
                }
                is IGitHubMod -> if (!checkUpdateToDate(lockFile = cacheFile, logger = logger)) {
                    mod.download(cacheFile)
                }
                is IDownloadableMod -> if (!cacheFile.exists()) {
                    mod.download(cacheFile)
                }
                else -> throw Exception("Unhandled mod $mod")
            }
            if (cacheFile.exists()) {
                createSymbolicLinkOrCopy(link = mod.resolveOutputFile(), target = cacheFile)
            } else {
                logger.error("$cacheFile doesn't exist.")
            }
        }
    }

    fun lock() {
        temporaryDir.resolve(lockFile)
    }

    fun IDownloadableMod.download(cacheFile: File) {
        logger.lifecycle("Downloading $this -> $cacheFile...")
        try {
            this.resolveDownloadSrc().openStream().copyToTmpAndMove(cacheFile)
            logger.info("$cacheFile was downloaded.")
        } catch (e: Exception) {
            logger.error("Failed to download $this", e)
        }
    }
}

data class ModLock(
    val path: String,
)