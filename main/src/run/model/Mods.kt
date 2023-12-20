package io.github.liplum.mindustry

import arc.util.serialization.Jval
import io.github.liplum.dsl.*
import io.github.liplum.mindustry.SharedCache.updateGitHubDownloadTrack
import org.gradle.api.GradleException
import java.io.File
import java.io.Serializable
import java.net.URL
import java.security.MessageDigest

/**
 * An abstract mod file.
 */
sealed interface IMod : Serializable {
    val fileName4Local: String
    fun resolveCacheFile(): File
}

sealed interface IDownloadableMod : IMod {
    /**
     * Generate a deterministic URL.
     * It can be an expensive task if it requests any data.
     */
    fun resolveDownloadSrc(): URL
}

sealed interface IGitHubMod : IDownloadableMod {
    override fun resolveCacheFile(): File {
        return SharedCache.modsDir.resolve("github").resolve(fileName4Local)
    }
}

/**
 * A local mod from disk.
 */
data class LocalMod(
    val modFile: File = File(""),
) : IMod {
    constructor(path: String) : this(File(path))

    override val fileName4Local: String = modFile.name

    override fun resolveCacheFile(): File = modFile
}

/**
 * A mod from a url.
 */
data class UrlMod(
    val url: URL,
) : IDownloadableMod {
    constructor(url: String) : this(URL(url))

    override fun resolveDownloadSrc() = url

    override val fileName4Local: String = "${url.resolve4FileName()}.zip"

    override fun resolveCacheFile(): File {
        return SharedCache.modsDir.resolve("url").resolve(fileName4Local)
    }
}


fun String.repo2Path() = this.replace("/", "-")

/**
 * A mod on GitHub.
 */
data class GitHubUntypedMod(
    /**
     * like "PlumyGames/mgpp"
     */
    val repo: String,
) : IGitHubMod {
    override fun resolveDownloadSrc(): URL {
        updateGitHubDownloadTrack(lockFile = resolveCacheFile())
        val jsonText = URL("https://api.github.com/repos/$repo").readText()
        val json = Jval.read(jsonText)
        val lan = json.getString("language")
        return if (isJvmMod(lan)) {
            resolveJvmModSrc(repo)
        } else {
            val mainBranch = json.getString("default_branch")
            resolvePlainModSrc(repo, mainBranch)
        }
    }

    override val fileName4Local = repo.repo2Path() + ".zip"
}


data class GitHubPlainMod(
    val repo: String, val branch: String? = null,
) : IGitHubMod {
    val fileNameWithoutExtension = linkString(separator = "-", repo.repo2Path(), branch)
    override fun resolveDownloadSrc(): URL {
        updateGitHubDownloadTrack(lockFile = resolveCacheFile())
        val jsonText = URL("https://api.github.com/repos/$repo").readText()
        val json = Jval.read(jsonText)
        val branch = if (!branch.isNullOrBlank()) branch
        else json.getString("default_branch")
        return resolvePlainModSrc(repo, branch)
    }

    override val fileName4Local = "$fileNameWithoutExtension.zip"
}


data class GitHubJvmMod(
    val repo: String,
    val tag: String? = null,
) : IGitHubMod {
    val fileNameWithoutExtension = linkString(separator = "-", repo.repo2Path(), tag)
    override fun resolveDownloadSrc(): URL {
        updateGitHubDownloadTrack(lockFile = resolveCacheFile())
        return if (tag == null) {
            resolveJvmModSrc(repo)
        } else {
            val releaseJson = URL("https://api.github.com/repos/$repo/releases").readText()
            val json = Jval.read(releaseJson)
            val releases = json.asArray()
            val release = releases.find { it.getString("tag_name") == tag }
                ?: throw GradleException("Tag<$tag> of $repo not found.")
            val url = URL(release.getString("url"))
            resolveJvmModSrc(url)
        }
    }

    override val fileName4Local = "$fileNameWithoutExtension.jar"
}

private
fun isJvmMod(lang: String) = lang == "Java" || lang == "Kotlin" ||
        lang == "Groovy" || lang == "Scala" ||
        lang == "Clojure"

private
fun resolveJvmModSrc(releaseEntryUrl: URL): URL {
    val releaseJson = releaseEntryUrl.readText()
    val json = Jval.read(releaseJson)
    val assets = json["assets"].asArray()
    val dexedAsset = assets.find {
        it.getString("name").startsWith("dexed") &&
                it.getString("name").endsWith(".jar")
    }
    val asset = dexedAsset ?: assets.find { it.getString("name").endsWith(".jar") }
    ?: throw GradleException("Failed to find the mod.")
    val url = asset.getString("browser_download_url")
    return URL(url)
}

private
fun resolveJvmModSrc(repo: String, tag: String = "latest"): URL {
    return resolveJvmModSrc(releaseEntryUrl = URL("https://api.github.com/repos/$repo/releases/$tag"))
}


internal
fun resolvePlainModSrc(repo: String, branch: String): URL {
    val url = "https://api.github.com/repos/$repo/zipball/$branch"
    return URL(url)
}

