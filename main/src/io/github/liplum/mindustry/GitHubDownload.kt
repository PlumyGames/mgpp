package io.github.liplum.mindustry

import java.io.File
import java.io.InputStream
import java.io.Serializable
import java.net.URL

/**
 * An abstract download location, which only can open an input stream for reading
 */
interface IDownloadLocation : Serializable {
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
data class LocalDownload(
    var localFile: File,
) : IDownloadLocation {
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
) : IDownloadLocation {
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