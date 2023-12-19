package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.dsl.copyTo
import io.github.liplum.dsl.listProp
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File
import kotlin.math.absoluteValue

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
                    is IGitHubMod -> mod.downloadOrUpdate(cacheFile)
                    is UrlMod -> mod.download(cacheFile)
                    else -> {}
                }
            }
            createSymbolicLinkOrCopyCache(link = mod.resolveOutputFile(), target = cacheFile)
        }
    }

    fun IGitHubMod.downloadOrUpdate(cacheFile: File) {
        if (!this.isUpdateToDate()) {
            val temp = File.createTempFile(this.fileName4Local, null)
            try {
                this.resolveDownloadSrc().openStream().use {
                    it.copyTo(temp)
                }
            } catch (e: Exception) {
                logger.warn("Failed to update $this", e)
            }
            temp.copyTo(cacheFile)
        }
    }

    fun IGitHubMod.isUpdateToDate(): Boolean {
        val cacheFile = this.resolveCacheFile()
        val infoFi = File("$cacheFile.$infoX")
        if (!cacheFile.exists()) {
            if (infoFi.exists()) infoFi.delete()
            return false
        }
        val meta = tryReadGitHubModInfo(infoFi)
        val curTime = System.currentTimeMillis()
        // TODO: Configurable out-of-date time
        return curTime - meta.lastUpdateTimestamp < R.outOfDataTime.absoluteValue
    }

    fun UrlMod.download(cacheFile: File) {
        url.copyTo(cacheFile)
    }
}
