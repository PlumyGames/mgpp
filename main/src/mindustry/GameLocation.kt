package io.github.liplum.mindustry

import java.io.File
import java.io.Serializable

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