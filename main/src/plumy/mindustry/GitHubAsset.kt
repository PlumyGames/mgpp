package plumy.mindustry

import java.io.Serializable
import java.net.URL

data class GitHubAsset(
    var name: String,
    var url: URL,
) : Serializable {
    companion object {
        fun release(
            user: String, repo: String,
            version: String,
            assetName: String,
        ) = GitHubAsset(
            assetName,
            URL("https://github.com/$user/$repo/releases/download/$version/$assetName")
        )
    }
}

inline fun GitHubAsset(
    user: String, repo: String,
    spec: GitHubAssetSpec.() -> Unit,
) {
    GitHubAssetSpec(user, repo).spec()
}

class GitHubAssetSpec(
    val user: String,
    val repo: String,
) {
    fun release(
        version: String,
        assetName: String,
    ) = GitHubAsset.release(user, repo, version, assetName)
}