package plumy.mindustry

import java.io.File
import java.io.InputStream
import java.io.Serializable
import java.net.URL

interface IDownloadLocation : Serializable {
    /**
     * Open an input stream for reading.
     * The caller has the responsibility to close this.
     */
    fun openInputStream(): InputStream
    val name: String
    val path:String
}

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

data class GitHubDownload(
    override var name: String,
    var url: URL,
) : IDownloadLocation {
    override val path: String
        get() = url.path
    companion object {
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

inline fun GitHubDownload(
    user: String, repo: String,
    spec: GitHubDownloadSpec.() -> Unit,
) {
    GitHubDownloadSpec(user, repo).spec()
}

class GitHubDownloadSpec(
    val user: String,
    val repo: String,
) {
    fun release(
        version: String,
        assetName: String,
    ) = GitHubDownload.release(user, repo, version, assetName)
}