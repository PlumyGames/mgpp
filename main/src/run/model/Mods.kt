package io.github.liplum.mindustry

import io.github.liplum.dsl.*
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

sealed interface IGitHubMod : IMod {
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
) : IMod {
    constructor(url: String) : this(URL(url))

    override val fileName4Local: String = run {
        val path: String = url.path
        val last = path.substring(path.lastIndexOf('/') + 1)
        if (last.endsWith(".zip")) last else "$last.zip"
    }

    override fun resolveCacheFile(): File {
        val urlInBytes = MessageDigest
            .getInstance("SHA-1")
            .digest(url.toString().toByteArray())
        val urlHashed = urlInBytes.toString()
        return SharedCache.modsDir.resolve("url").resolve(urlHashed)
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
data class GitHubUntypedMod(
    /**
     * like "PlumyGames/mgpp"
     */
    val repo: String,
) : IGitHubMod {
    override val fileName4Local = repo.repo2Path() + ".zip"
}


data class GitHubPlainMod(
    val repo: String, val branch: String? = null,
) : IGitHubMod {
    val fileNameWithoutExtension = linkString(separator = "-", repo.repo2Path(), branch)
    override val fileName4Local = "$fileNameWithoutExtension.zip"
}


data class GitHubJvmMod(
    val repo: String,
    val tag: String? = null,
) : IGitHubMod {
    val fileNameWithoutExtension = linkString(separator = "-", repo.repo2Path(), tag)
    override val fileName4Local = "$fileNameWithoutExtension.jar"
}
