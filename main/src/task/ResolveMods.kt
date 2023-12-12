package io.github.liplum.mindustry

import arc.util.serialization.Jval
import io.github.liplum.dsl.*
import io.github.liplum.dsl.copyTo
import io.github.liplum.dsl.listProp
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.net.URL
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
                this.downloadTo(temp)
            } catch (e: Exception) {
                logger.warn("Failed to update $this", e)
            }
            temp.copyTo(cacheFile)
        }
    }

    fun IGitHubMod.downloadTo(cacheFile: File) {
        when (this) {
            is GitHubUntypedMod -> {
                updateGitHubModUpdateToDate(modFile = cacheFile, logger = logger)
                val jsonText = URL("https://api.github.com/repos/$repo").readText()
                val json = Jval.read(jsonText)
                val lan = json.getString("language")
                if (lan.isJvmMod()) {
                    importJvmMod(repo, writeIn = cacheFile)
                } else {
                    val mainBranch = json.getString("default_branch")
                    importPlainMod(repo, mainBranch, cacheFile)
                }
            }

            is GitHubJvmMod -> {
                updateGitHubModUpdateToDate(modFile = cacheFile, logger = logger)
                if (tag == null) {
                    importJvmMod(repo, writeIn = cacheFile)
                } else {
                    val releaseJson = URL("https://api.github.com/repos/$repo/releases").readText()
                    val json = Jval.read(releaseJson)
                    val releases = json.asArray()
                    val release = releases.find { it.getString("tag_name") == tag }
                        ?: throw GradleException("Tag<$tag> of $repo not found.")
                    val url = URL(release.getString("url"))
                    importJvmMod(url, cacheFile)
                }
            }

            is GitHubPlainMod -> {
                updateGitHubModUpdateToDate(modFile = cacheFile, logger = logger)
                val jsonText = URL("https://api.github.com/repos/$repo").readText()
                val json = Jval.read(jsonText)
                val branch = if (!branch.isNullOrBlank()) branch
                else json.getString("default_branch")
                importPlainMod(repo, branch, cacheFile)
            }

            else -> {}
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

private
fun String.isJvmMod() = this == "Java" || this == "Kotlin" ||
        this == "Groovy" || this == "Scala" ||
        this == "Clojure"

private
fun importJvmMod(releaseEntryUrl: URL, writeIn: File) {
    val releaseJson = releaseEntryUrl.readText()
    val json = Jval.read(releaseJson)
    val assets = json["assets"].asArray()
    val dexedAsset = assets.find {
        it.getString("name").startsWith("dexed") &&
                it.getString("name").endsWith(".jar")
    }
    val asset = dexedAsset ?: assets.find { it.getString("name").endsWith(".jar") }
    if (asset != null) {
        val url = asset.getString("browser_download_url")
        URL(url).copyTo(writeIn)
    } else {
        throw GradleException("Failed to find the mod.")
    }
}

private
fun importJvmMod(repo: String, tag: String = "latest", writeIn: File) {
    importJvmMod(releaseEntryUrl = URL("https://api.github.com/repos/$repo/releases/$tag"), writeIn)
}


internal
fun importPlainMod(repo: String, branch: String, dest: File) {
    val url = "https://api.github.com/repos/$repo/zipball/$branch"
    URL(url).copyTo(dest)
}


internal fun updateGitHubModUpdateToDate(
    modFile: File,
    newTimestamp: Long = System.currentTimeMillis(),
    logger: Logger? = null,
) {
    val infoFi = File("$modFile.$infoX")
    if (infoFi.isDirectory) {
        infoFi.deleteRecursively()
    }
    val meta = GihHubModDownloadMeta(lastUpdateTimestamp = newTimestamp)
    val json = gson.toJson(meta)
    try {
        infoFi.writeText(json)
    } catch (e: Exception) {
        logger?.warn("Failed to write into \"info.json\"", e)
    }
}

internal
fun tryReadGitHubModInfo(infoFi: File, logger: Logger? = null): GihHubModDownloadMeta {
    fun writeAndGetDefault(): GihHubModDownloadMeta {
        val meta = GihHubModDownloadMeta(lastUpdateTimestamp = System.currentTimeMillis())
        val infoContent = gson.toJson(meta)
        try {
            infoFi.ensureParentDir().writeText(infoContent)
            logger?.info("[MGPP] $infoFi is created.")
        } catch (e: Exception) {
            logger?.warn("Failed to write into \"info.json\"", e)
        }
        return meta
    }
    return if (infoFi.isFile) {
        try {
            val infoContent = infoFi.readText()
            gson.fromJson(infoContent)
        } catch (e: Exception) {
            writeAndGetDefault()
        }
    } else {
        writeAndGetDefault()
    }
}