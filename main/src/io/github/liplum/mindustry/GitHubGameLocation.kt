package io.github.liplum.mindustry

import java.io.File
import java.io.Serializable

interface IGameLocation : Serializable {
    var fileName: String
    fun toDownloadLocation(): IDownloadLocation
    infix fun named(name: String): IGameLocation {
        fileName = name
        return this
    }
}

data class GitHubGameLocation(
    val user: String = "",
    val repo: String = "",
    val version: String = "",
    val release: String = "",
) : IGameLocation {
    val download = GitHubDownload.release(user, repo, version, release)
    override var fileName = "${download.name.removeSuffix(".jar")}-${user}-${repo}-${version}.jar"
    override fun toDownloadLocation() = download
}

data class LocalGameLocation(
    val file: File,
) : IGameLocation {
    val localCopy = LocalDownload(file)
    override var fileName: String = file.name
    override fun toDownloadLocation() = localCopy
}