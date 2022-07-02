package io.github.liplum.mindustry

import arc.util.serialization.Jval
import io.github.liplum.dsl.copyTo
import java.io.File
import java.io.Serializable
import java.net.URL

interface IMod : Serializable {
    fun resolveFile(currentDir: File): File
    fun mapLocalFile(currentDir: File): File
}

data class LocalMod(
    var modFile: File = File(""),
) : IMod {
    constructor(path: String) : this(File(path))

    override fun resolveFile(currentDir: File): File =
        currentDir.resolve(modFile.name).apply {
            modFile.copyTo(this)
        }

    override fun mapLocalFile(currentDir: File): File =
        currentDir.resolve(modFile.name)
}

data class UrlMod(
    var url: URL,
) : IMod {
    constructor(url: String) : this(URL(url))

    override fun resolveFile(currentDir: File): File {
        val path: String = url.toURI().path
        val last = path.substring(path.lastIndexOf('/') + 1)
        val name = if (last.endsWith(".jar")) last else "$last.jar"
        return currentDir.resolve(name).apply {
            url.copyTo(this)
        }
    }

    override fun mapLocalFile(currentDir: File): File {
        val path: String = url.toURI().path
        val last = path.substring(path.lastIndexOf('/') + 1)
        val name = if (last.endsWith(".jar")) last else "$last.jar"
        return currentDir.resolve(name)
    }
}

data class GitHubMod(
    var repo: String,
) : IMod {
    override fun resolveFile(currentDir: File): File {
        val releaseJson = URL("https://api.github.com/repos/$repo/releases/latest").readText()
        val json = Jval.read(releaseJson)
        val assets = json["assets"].asArray()
        val dexedAsset = assets.find {
            it.getString("name").startsWith("dexed") &&
                    it.getString("name").endsWith(".jar")
        }
        val asset = dexedAsset ?: assets.find { it.getString("name").endsWith(".jar") }
        if (asset != null) {
            val url = asset.getString("browser_download_url")
            val modFile = currentDir.resolve(repo.replace("/", "-") + ".jar")
            URL(url).copyTo(modFile)
            return modFile
        } else {
            throw RuntimeException("Can't find the mod.")
        }
    }

    override fun mapLocalFile(currentDir: File): File =
        currentDir.resolve(repo.replace("/", "-") + ".jar")
}
