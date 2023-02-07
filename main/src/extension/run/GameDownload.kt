package io.github.liplum.mindustry

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

data class LatestOfficialMindustryLoc(
    val file: String
) : IGameLoc {
    override val fileName: String
        get() = TODO("Not yet implemented")

    override fun createDownloadLoc(): IDownloadLoc {
        TODO("Not yet implemented")
    }

    override fun resolveOutputFile(): File {
        TODO("Not yet implemented")
    }
}

data class LatestBeMindustryLoc(
    val file: String
) : IGameLoc {
    override val fileName: String
        get() = TODO("Not yet implemented")

    override fun createDownloadLoc(): IDownloadLoc {
        TODO("Not yet implemented")
    }

    override fun resolveOutputFile(): File {
        TODO("Not yet implemented")
    }
}

data class LocalGameLoc(
    val file: File,
) : IGameLoc {
    constructor(path: String) : this(File(path))

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