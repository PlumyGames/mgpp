package io.github.liplum.mindustry

import arc.util.serialization.Jval
import org.gradle.api.GradleException
import java.io.File
import java.io.InputStream
import java.io.Serializable
import java.net.URL

/**
 * An abstract Mindustry game file.
 */
interface IGameLoc : Serializable {
    val fileName: String
    /**
     * Generate an [IDownloadLoc] deterministically.
     */
    fun createDownloadLoc(): IDownloadLoc

    fun resolveOutputFile(): File
}

data class GitHubGameLoc(
    val user: String,
    val repo: String,
    val tag: String,
    val file: String,
) : IGameLoc {
    val download = GitHubDownload.release(user, repo, tag, file)
    override val fileName = "$user-$repo-$tag-${download.name}"
    override fun createDownloadLoc() = download
    override fun resolveOutputFile(): File {
        return SharedCache.gamesDir.resolve("github").resolve(fileName)
    }
}

enum class MindustryEnd {
    Client, Server
}

data class LatestOfficialMindustryLoc(
    val end: MindustryEnd
) : IGameLoc {
    val fileBasename = when (end) {
        MindustryEnd.Client -> R.officialRelease.client
        MindustryEnd.Server -> R.officialRelease.server
    }
    override val fileName = "${R.github.anuken}-${R.github.mindustry}-latest-$fileBasename"

    override fun createDownloadLoc(): IDownloadLoc {
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
        return delegate.createDownloadLoc()
    }

    override fun resolveOutputFile(): File {
        return SharedCache.gamesDir.resolve("github").resolve(fileName)
    }
}

data class LatestMindustryBELoc(
    val end: MindustryEnd
) : IGameLoc {
    val fileBasename = when (end) {
        MindustryEnd.Client -> R.beRelease.client()
        MindustryEnd.Server -> R.beRelease.server()
    }
    override val fileName = "${R.github.anuken}-${R.github.mindustryBuilds}-latest-$fileBasename"

    override fun createDownloadLoc(): IDownloadLoc {
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
        return delegate.createDownloadLoc()
    }

    override fun resolveOutputFile(): File {
        return SharedCache.gamesDir.resolve("github").resolve(fileName)
    }
}

data class LocalGameLoc(
    val file: File,
) : IGameLoc {
    override val fileName: String = file.name
    val localCopy = LocalCopy(file)
    override fun createDownloadLoc() = localCopy
    /**
     * It points to a local file
     */
    override fun resolveOutputFile(): File {
        return file
    }
}


/**
 * An abstract download location, which can only open the input stream for reading
 */
interface IDownloadLoc : Serializable {
    /**
     * Open an input stream for reading.
     * The caller has the responsibility to close this.
     */
    fun openInputStream(): InputStream
    /**
     * The name of download location.
     */
    val name: String
    /**
     * The path of download location.
     */
    val path: String
}
/**
 * A local download from disk
 */
data class LocalCopy(
    var localFile: File,
) : IDownloadLoc {
    override val name: String
        get() = localFile.name
    override val path: String
        get() = localFile.absolutePath

    override fun openInputStream(): InputStream =
        localFile.inputStream()
}
/**
 * A download from any GitHub url
 */
data class GitHubDownload(
    override var name: String,
    var url: URL,
) : IDownloadLoc {
    override val path: String
        get() = url.toString()

    companion object {
        @JvmStatic
        fun release(
            user: String, repo: String,
            version: String,
            assetName: String,
        ) = GitHubDownload(
            assetName,
            URL("https://github.com/$user/$repo/releases/download/$version/$assetName")
        )
    }

    override fun openInputStream(): InputStream =
        url.openStream()
}