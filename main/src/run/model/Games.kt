package io.github.liplum.mindustry

import arc.util.serialization.Jval
import org.gradle.api.GradleException
import java.io.File
import java.io.Serializable
import java.net.URL

/**
 * An abstract Mindustry game file.
 */
sealed interface IGameLoc : Serializable {
    val fileName4Local: String
    fun resolveCacheFile(): File
}

sealed interface IDownloadableGameLoc : IGameLoc {
    /**
     * Generate a deterministic URL.
     * It can be an expensive task if it requests any data.
     */
    fun resolveDownloadSrc(): URL
}

sealed interface ILatestDownloadableGameLoc : IDownloadableGameLoc

data class UrlGameLoc(
    val url: URL
) : IDownloadableGameLoc {
    override fun resolveDownloadSrc(): URL = url

    override val fileName4Local: String = "${url.resolve4FileName()}.jar"

    override fun resolveCacheFile(): File {
        return SharedCache.gamesDir.resolve("url").resolve(fileName4Local)
    }
}

data class GitHubGameLoc(
    val user: String,
    val repo: String,
    val tag: String,
    val file: String,
) : IDownloadableGameLoc {
    override val fileName4Local = "$user-$repo-$tag-${file}"
    override fun resolveDownloadSrc() = URL("https://github.com/$user/$repo/releases/download/$tag/$file")
    override fun resolveCacheFile(): File {
        return SharedCache.gamesDir.resolve("github").resolve(fileName4Local)
    }
}

enum class MindustryEnd {
    Client, Server
}

data class LatestOfficialMindustryLoc(
    val end: MindustryEnd
) : ILatestDownloadableGameLoc {
    val fileBasename = when (end) {
        MindustryEnd.Client -> R.officialRelease.client
        MindustryEnd.Server -> R.officialRelease.server
    }
    override val fileName4Local = "${R.github.anuken}-${R.github.mindustry}-latest-$fileBasename"

    override fun resolveDownloadSrc(): URL {
        val url = URL(R.github.tag.latestReleaseAPI)
        val json = Jval.read(url.readText())
        val version = json.getString("tag_name")
            ?: throw GradleException("Failed to resolve latest version of official Mindustry.")
        val delegate = GitHubGameLoc(
            user = R.github.anuken,
            repo = R.github.mindustry,
            tag = version,
            file = when (end) {
                MindustryEnd.Client -> R.officialRelease.client
                MindustryEnd.Server -> R.officialRelease.server
            }
        )
        return delegate.resolveDownloadSrc()
    }

    override fun resolveCacheFile(): File {
        return SharedCache.gamesDir.resolve("github").resolve(fileName4Local)
    }
}

data class LatestMindustryBELoc(
    val end: MindustryEnd
) : ILatestDownloadableGameLoc {
    val fileBasename = when (end) {
        MindustryEnd.Client -> R.beRelease.client()
        MindustryEnd.Server -> R.beRelease.server()
    }
    override val fileName4Local = "${R.github.anuken}-${R.github.mindustryBuilds}-latest-$fileBasename"

    override fun resolveDownloadSrc(): URL {
        val url = URL(R.github.tag.beLatestReleaseAPI)
        val json = Jval.read(url.readText())
        val version = json.getString("tag_name")
            ?: throw GradleException("Failed to resolve latest version of Mindustry Bleeding Edge.")
        val delegate = GitHubGameLoc(
            user = R.github.anuken,
            repo = R.github.mindustryBuilds,
            tag = version,
            file = when (end) {
                MindustryEnd.Client -> R.beRelease.client(version = version)
                MindustryEnd.Server -> R.beRelease.server(version = version)
            }
        )
        return delegate.resolveDownloadSrc()
    }

    override fun resolveCacheFile(): File {
        return SharedCache.gamesDir.resolve("github").resolve(fileName4Local)
    }
}

data class LocalGameLoc(
    val file: File,
) : IGameLoc {
    constructor(path: String) : this(File(path))

    override val fileName4Local: String = file.name

    /**
     * It points to a local file
     */
    override fun resolveCacheFile(): File {
        return file
    }
}

