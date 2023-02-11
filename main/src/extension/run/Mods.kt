package io.github.liplum.mindustry

import arc.util.serialization.Jval
import io.github.liplum.dsl.*
import org.gradle.api.GradleException
import java.io.File
import java.io.Serializable
import java.net.URL

/**
 * An abstract mod file.
 */
sealed interface IMod : Serializable

sealed interface IDownloadableMod : IMod {
    val fileName: String
    fun resolveFile(writeIn: File)
    fun isUpdateToDate(modFile: File): Boolean
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

    override fun isUpdateToDate(modFile: File): Boolean {
        if (!modFile.exists()) return false
        return true
    }
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
    override val fileName = repo.repo2Path() + ".zip"

    override fun resolveFile(writeIn: File) {
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

    override fun isUpdateToDate(modFile: File): Boolean {
        if (!modFile.exists()) return false
        return true
    }
}

data class GitHubJvmMod(
    val repo: String,
    val tag: String? = null,
) : IDownloadableMod {
    override val fileName = repo.repo2Path() + ".jar"

    override fun resolveFile(writeIn: File) {
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

    override fun isUpdateToDate(modFile: File): Boolean {
        if (!modFile.exists()) return false
        return true
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
        throw GradleException("Can't find the mod.")
    }
}
private
fun importJvmMod(repo: String, tag: String = "latest", writeIn: File) {
    importJvmMod(releaseEntryUrl = "https://api.github.com/repos/$repo/releases/$tag", writeIn)
}

data class GitHubPlainMod(
    val repo: String, val branch: String? = null,
) : IDownloadableMod {
    override val fileName = linkString(separator = "-", repo.repo2Path(), branch) + ".zip"

    override fun resolveFile(writeIn: File) {
        val jsonText = URL("https://api.github.com/repos/$repo").readText()
        val json = Jval.read(jsonText)
        val branch = if (!branch.isNullOrBlank()) branch
        else json.getString("default_branch")
        importPlainMod(repo, branch, writeIn)
    }

    override fun isUpdateToDate(modFile: File): Boolean {
        if (!modFile.exists()) return false
        return true
    }
}

internal
fun importPlainMod(repo: String, branch: String, dest: File) {
    val url = "https://api.github.com/repos/$repo/zipball/$branch"
    URL(url).copyTo(dest)
}
