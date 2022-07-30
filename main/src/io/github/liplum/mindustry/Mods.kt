package io.github.liplum.mindustry

import arc.util.serialization.Jval
import io.github.liplum.dsl.*
import org.gradle.api.Project
import java.io.File
import java.io.Serializable
import java.net.URL

/**
 * An abstract mod file.
 */
interface IMod : Serializable {
    fun resolveFile(project: Project, currentDir: File): List<File>
    fun mapLocalFile(project: Project, currentDir: File): List<File>
}
/**
 * A local mod from disk.
 */
data class LocalMod(
    var modFile: File = File(""),
) : IMod {
    constructor(path: String) : this(File(path))

    override fun resolveFile(project: Project, currentDir: File): List<File> =
        listOf(currentDir.resolve(modFile.name).apply {
            modFile.copyTo(this, overwrite = true)
        })

    override fun mapLocalFile(project: Project, currentDir: File): List<File> =
        listOf(currentDir.resolve(modFile.name))
}
/**
 * A local mod from disk.
 */
data class ModFolder(
    var folder: File = File(""),
) : IMod {
    constructor(path: String) : this(File(path))

    override fun resolveFile(project: Project, currentDir: File): List<File> =
        folder.getOrCreateDir().getFilesRecursive().map {
            it.copyTo(currentDir.resolve(it.name), overwrite = true)
        }

    override fun mapLocalFile(project: Project, currentDir: File): List<File> =
        folder.getOrCreateDir().getFilesRecursive().map {
            currentDir.resolve(it.name)
        }
}
/**
 * A mod from a url.
 */
data class UrlMod(
    var url: URL,
) : IMod {
    constructor(url: String) : this(URL(url))

    val fileName: String
        get() {
            val path: String = url.toURI().path
            val last = path.substring(path.lastIndexOf('/') + 1)
            return if (last.endsWith(".jar")) last else "$last.jar"
        }

    override fun resolveFile(project: Project, currentDir: File): List<File> =
        listOf(currentDir.resolve(fileName).apply {
            url.copyTo(this)
        })

    override fun mapLocalFile(project: Project, currentDir: File): List<File> =
        listOf(currentDir.resolve(fileName))
}

fun String.isJvmMod() = this == "Java" || this == "Kotlin" ||
        this == "Groovy" || this == "Scala" ||
        this == "Clojure"

fun importJvmMod(repo: String, dest: File) {
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
        URL(url).copyTo(dest)
    } else {
        throw RuntimeException("Can't find the mod.")
    }
}

fun importPlainMod(repo: String, branch: String, dest: File) {
    val url = "https://api.github.com/repos/$repo/zipball/$branch"
    URL(url).copyTo(dest)
}

fun String.repo2Path() = this.replace("/", "-")
/**
 * A mod on GitHub.
 */
data class GitHubMod(
    /**
     * like "PlumyGame/mgpp"
     */
    var repo: String,
) : IMod {
    val fileName: String
        get() = repo.repo2Path() + ".zip"

    override fun resolveFile(project: Project, currentDir: File): List<File> {
        val jsonText = URL("https://api.github.com/repos/$repo").readText()
        val json = Jval.read(jsonText)
        val lan = json.getString("language")
        val modFile = currentDir.resolve(fileName)
        if (lan.isJvmMod()) {
            importJvmMod(repo, modFile)
        } else {
            val mainBranch = json.getString("default_branch")
            importPlainMod(repo, mainBranch, modFile)
        }
        return listOf(modFile)
    }

    override fun mapLocalFile(project: Project, currentDir: File): List<File> =
        listOf(currentDir.resolve(fileName))
}

data class GitHubJvmMod(
    var repo: String,
) : IMod {
    val fileName: String
        get() = repo.repo2Path() + ".jar"

    override fun resolveFile(project: Project, currentDir: File): List<File> {
        val modFile = currentDir.resolve(fileName)
        importJvmMod(repo, modFile)
        return listOf(modFile)
    }

    override fun mapLocalFile(project: Project, currentDir: File): List<File> =
        listOf(currentDir.resolve(fileName))
}

data class GitHubPlainMod(
    var repo: String, var branch: String = "",
) : IMod {
    val fileName: String
        get() = linkString(separator = "-", repo.repo2Path(), branch) + ".zip"

    override fun resolveFile(project: Project, currentDir: File): List<File> {
        val jsonText = URL("https://api.github.com/repos/$repo").readText()
        val json = Jval.read(jsonText)
        val branch = branch.ifBlank { json.getString("default_branch") }
        val modFile = currentDir.resolve(fileName)
        importPlainMod(repo, branch, modFile)
        return listOf(modFile)
    }

    override fun mapLocalFile(project: Project, currentDir: File): List<File> =
        listOf(currentDir.resolve(fileName))

    infix fun branch(branch: String) {
        this.branch = branch
    }
}