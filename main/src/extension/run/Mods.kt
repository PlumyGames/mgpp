package io.github.liplum.mindustry

import arc.util.serialization.Jval
import io.github.liplum.dsl.*
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import java.io.File
import java.io.Serializable
import java.net.URL
import kotlin.math.absoluteValue

internal
const val infoX = "info.json"
/**
 * An abstract mod file.
 */
sealed interface IMod : Serializable

sealed interface IDownloadableMod : IMod {
    val fileName: String
    fun resolveFile(writeIn: File, logger: Logger? = null)
}

sealed interface IGitHubMod : IDownloadableMod {
    fun updateFile(writeIn: File, logger: Logger? = null)
    fun isUpdateToDate(modFile: File, logger: Logger? = null): Boolean
}

/**
 * A local mod from disk.
 */
data class LocalMod(
    val modFile: File = File(""),
) : IMod {
    constructor(path: String) : this(File(path))
}

/**
 * A mod from a url.
 */
data class UrlMod(
    val url: URL,
) : IDownloadableMod {
    constructor(url: String) : this(URL(url))

    override val fileName: String
        get() {
            val path: String = url.toURI().path
            val last = path.substring(path.lastIndexOf('/') + 1)
            return if (last.endsWith(".zip")) last else "$last.zip"
        }

    override fun resolveFile(writeIn: File, logger: Logger?) {
        url.copyTo(writeIn)
    }
}


fun String.repo2Path() = this.replace("/", "-")
data class GihHubModDownloadMeta(
    /**
     * It's changed when the mod is updated or network error.
     */
    val lastUpdateTimestamp: Long
)
/**
 * A mod on GitHub.
 */
data class GitHubMod(
    /**
     * like "PlumyGames/mgpp"
     */
    val repo: String,
) : IGitHubMod {
    override val fileName = repo.repo2Path() + ".zip"

    override fun resolveFile(writeIn: File, logger: Logger?) {
        updateGitHubModUpdateToDate(modFile = writeIn, logger = logger)
        val jsonText = URL("https://api.github.com/repos/$repo").readText()
        val json = Jval.read(jsonText)
        val lan = json.getString("language")
        if (lan.isJvmMod()) {
            importJvmMod(repo, writeIn = writeIn)
        } else {
            val mainBranch = json.getString("default_branch")
            importPlainMod(repo, mainBranch, writeIn)
        }
    }

    override fun updateFile(writeIn: File, logger: Logger?) {
        val temp = File.createTempFile(repo.repo2Path(), "zip")
        resolveFile(writeIn = temp, logger = logger)
        temp.copyTo(writeIn)
    }

    override fun isUpdateToDate(modFile: File, logger: Logger?): Boolean {
        return validateGitHubModUpdateToDate(modFile, logger = logger)
    }
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
fun validateGitHubModUpdateToDate(
    modFile: File,
    logger: Logger? = null,
): Boolean {
    val infoFi = File("$modFile.$infoX")
    if (!modFile.exists()) {
        if (infoFi.exists()) infoFi.delete()
        return false
    }
    val meta = tryReadGitHubModInfo(infoFi)
    val curTime = System.currentTimeMillis()
    // TODO: Configurable out-of-date time
    return curTime - meta.lastUpdateTimestamp < R.outOfDataTime.absoluteValue
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

data class GitHubJvmMod(
    val repo: String,
    val tag: String? = null,
) : IGitHubMod {
    val fileNameWithoutExtension = linkString(separator = "-", repo.repo2Path(), tag)
    override val fileName = "$fileNameWithoutExtension.jar"

    override fun resolveFile(writeIn: File, logger: Logger?) {
        updateGitHubModUpdateToDate(modFile = writeIn, logger = logger)
        if (tag == null) {
            importJvmMod(repo, writeIn = writeIn)
        } else {
            val releaseJson = URL("https://api.github.com/repos/$repo/releases").readText()
            val json = Jval.read(releaseJson)
            val releases = json.asArray()
            val release = releases.find { it.getString("tag_name") == tag }
                ?: throw GradleException("Tag<$tag> of $repo not found.")
            val url = release.getString("url")
            importJvmMod(url, writeIn)
        }
    }

    override fun updateFile(writeIn: File, logger: Logger?) {
        resolveFile(writeIn = writeIn, logger = logger)
    }

    override fun isUpdateToDate(modFile: File, logger: Logger?): Boolean {
        return validateGitHubModUpdateToDate(modFile, logger = logger)
    }
}

private
fun String.isJvmMod() = this == "Java" || this == "Kotlin" ||
    this == "Groovy" || this == "Scala" ||
    this == "Clojure"

private
fun importJvmMod(releaseEntryUrl: String, writeIn: File) {
    val releaseJson = URL(releaseEntryUrl).readText()
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
    importJvmMod(releaseEntryUrl = "https://api.github.com/repos/$repo/releases/$tag", writeIn)
}

data class GitHubPlainMod(
    val repo: String, val branch: String? = null,
) : IGitHubMod {
    val fileNameWithoutExtension = linkString(separator = "-", repo.repo2Path(), branch)
    override val fileName = "$fileNameWithoutExtension.zip"

    override fun resolveFile(writeIn: File, logger: Logger?) {
        updateGitHubModUpdateToDate(modFile = writeIn, logger = logger)
        val jsonText = URL("https://api.github.com/repos/$repo").readText()
        val json = Jval.read(jsonText)
        val branch = if (!branch.isNullOrBlank()) branch
        else json.getString("default_branch")
        importPlainMod(repo, branch, writeIn)
    }

    override fun updateFile(writeIn: File, logger: Logger?) {
        resolveFile(writeIn = writeIn, logger = logger)
    }

    override fun isUpdateToDate(modFile: File, logger: Logger?): Boolean {
        return validateGitHubModUpdateToDate(modFile, logger = logger)
    }
}

internal
fun importPlainMod(repo: String, branch: String, dest: File) {
    val url = "https://api.github.com/repos/$repo/zipball/$branch"
    URL(url).copyTo(dest)
}
