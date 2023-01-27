package io.github.liplum.mindustry

import java.io.File
import java.io.Serializable

/**
 * An abstract Mindustry game file.
 */
interface IGameLoc : Serializable {
    var fileName: String
    /**
     * Generate an [IDownloadLoc] deterministically.
     */
    fun createDownloadLoc(): IDownloadLoc
    infix fun named(name: String): IGameLoc {
        fileName = name
        return this
    }
}

data class GitHubGameLoc(
    val user: String,
    val repo: String,
    val tag: String,
    val file: String,
) : IGameLoc {
    val download = GitHubDownload.release(user, repo, tag, file)
    override var fileName = "${download.name.removeSuffix(".jar")}-${user}-${repo}-${tag}.jar"
    override fun createDownloadLoc() = download
}

data class LocalGameLoc(
    val file: File,
) : IGameLoc {
    constructor(path: String) : this(File(path))

    val localCopy = LocalCopy(file)
    override var fileName: String = file.name
    override fun createDownloadLoc() = localCopy
}