package io.github.liplum.mindustry

import arc.util.serialization.Jval
import io.github.liplum.dsl.*
import java.io.File
import java.io.Serializable
import java.net.URL

/**
 * An abstract mod file.
 */
interface IMod : Serializable

interface IDownloadableMod : IMod {
    val fileName: String
    fun resolveFile(writeIn: File)
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

    override fun resolveFile(writeIn: File) {
        url.copyTo(writeIn)
    }
}

fun String.isJvmMod() = this == "Java" || this == "Kotlin" ||
    this == "Groovy" || this == "Scala" ||
    this == "Clojure"

fun importJvmMod(repo: String, dest: File) {
    val releaseJson = URL("https://api.github.com/repos/$repo/releases/latest").readText()
    val json = Jval.read(releaseJson)
    val assets = json["assets"].asArray()
    val dexedAsset = assets.find {
        it.getString("name").startsWith("dexed") &&
            it.getString("name").endsWith(".jar")
    }
    val asset = dexedAsset ?: assets.find { it.getString("name").endsWith(".jar") }
    if (asset != null) {
        val url = asset.getString("browser_download_url")
        URL(url).copyTo(dest)
    } else {
        throw RuntimeException("Can't find the mod.")
    }
}

fun importPlainMod(repo: String, branch: String, dest: File) {
    val url = "https://api.github.com/repos/$repo/zipball/$branch"
    URL(url).copyTo(dest)
}

fun String.repo2Path() = this.replace("/", "-")
/**
 * A mod on GitHub.
 */
data class GitHubMod(
    /**
     * like "PlumyGames/mgpp"
     */
    val repo: String,
) : IDownloadableMod {
    override val fileName: String
        get() = repo.repo2Path() + ".zip"

    override fun resolveFile(writeIn: File) {
        val jsonText = URL("https://api.github.com/repos/$repo").readText()
        val json = Jval.read(jsonText)
        val lan = json.getString("language")
        if (lan.isJvmMod()) {
            importJvmMod(repo, writeIn)
        } else {
            val mainBranch = json.getString("default_branch")
            importPlainMod(repo, mainBranch, writeIn)
        }
    }
}

data class GitHubJvmMod(
    val repo: String,
) : IDownloadableMod {
    override val fileName: String
        get() = repo.repo2Path() + ".jar"

    override fun resolveFile(writeIn: File) {
        importJvmMod(repo, writeIn)
    }
}

data class GitHubPlainMod(
    val repo: String, val branch: String? = null,
) : IDownloadableMod {
    override val fileName: String
        get() = linkString(separator = "-", repo.repo2Path(), branch) + ".zip"

    override fun resolveFile(writeIn: File) {
        val jsonText = URL("https://api.github.com/repos/$repo").readText()
        val json = Jval.read(jsonText)
        val branch = if (!branch.isNullOrBlank()) branch
        else json.getString("default_branch")
        importPlainMod(repo, branch, writeIn)
    }
}