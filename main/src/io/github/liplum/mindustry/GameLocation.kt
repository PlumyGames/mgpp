package io.github.liplum.mindustry

import java.io.File
import java.io.Serializable

/**
 * An abstract Mindustry game file.
 */
interface IGameLoc<T : IDownloadLoc> : Serializable {
    var fileName: String
    /**
     * Generate an [T] deterministically.
     */
    fun createDownloadLoc(): T
    infix fun named(name: String): IGameLoc<T> {
        fileName = name
        return this
    }
}

data class GitHubGameLoc(
    val user: String = "",
    val repo: String = "",
    val version: String = "",
    val release: String = "",
) : IGameLoc<GitHubDownload> {
    val download = GitHubDownload.release(user, repo, version, release)
    override var fileName = "${download.name.removeSuffix(".jar")}-${user}-${repo}-${version}.jar"
    override fun createDownloadLoc() = download
}

data class LocalGameLoc(
    val file: File,
) : IGameLoc<LocalCopy> {
    constructor(path: String) : this(File(path))

    val localCopy = LocalCopy(file)
    override var fileName: String = file.name
    override fun createDownloadLoc() = localCopy
}