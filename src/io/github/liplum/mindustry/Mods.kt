package io.github.liplum.mindustry

import arc.util.serialization.Jval
import io.github.liplum.dsl.copyTo
import io.github.liplum.mindustry.task.ResolveMods
import org.gradle.api.Project
import java.io.File
import java.io.Serializable
import java.net.URL

interface IMod : Serializable {
    fun preProcess(resolveMods: ResolveMods) {}
    fun resolveFile(project: Project, currentDir: File): File
    fun mapLocalFile(project: Project, currentDir: File): File
}

data class LocalMod(
    var modFile: File = File(""),
) : IMod {
    constructor(path: String) : this(File(path))

    override fun resolveFile(project: Project, currentDir: File): File =
        currentDir.resolve(modFile.name).apply {
            modFile.copyTo(this)
        }

    override fun mapLocalFile(project: Project, currentDir: File): File =
        currentDir.resolve(modFile.name)
}

data class UrlMod(
    var url: URL,
) : IMod {
    constructor(url: String) : this(URL(url))

    override fun resolveFile(project: Project, currentDir: File): File {
        val path: String = url.toURI().path
        val last = path.substring(path.lastIndexOf('/') + 1)
        val name = if (last.endsWith(".jar")) last else "$last.jar"
        return currentDir.resolve(name).apply {
            url.copyTo(this)
        }
    }

    override fun mapLocalFile(project: Project, currentDir: File): File {
        val path: String = url.toURI().path
        val last = path.substring(path.lastIndexOf('/') + 1)
        val name = if (last.endsWith(".jar")) last else "$last.jar"
        return currentDir.resolve(name)
    }
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
    val url = "https://api.github.com/repos/$repo/zipball/$branch:"
    URL(url).copyTo(dest)
}

data class GitHubMod(
    var repo: String,
) : IMod {
    override fun resolveFile(project: Project, currentDir: File): File {
        val jsonText = URL("https://api.github.com/repos/$repo").readText()
        val json = Jval.read(jsonText)
        val lan = json.getString("language")
        val modFile = currentDir.resolve(repo.replace("/", "-") + ".zip")
        if (lan.isJvmMod()) {
            importJvmMod(repo, modFile)
        } else {
            val mainBranch = json.getString("default_branch")
            importPlainMod(repo, mainBranch, modFile)
        }
        return modFile
    }

    override fun mapLocalFile(project: Project, currentDir: File): File =
        currentDir.resolve(repo.replace("/", "-") + ".zip")
}

data class GitHubJvmMod(
    var repo: String,
) : IMod {
    override fun resolveFile(project: Project, currentDir: File): File {
        val modFile = currentDir.resolve(repo.replace("/", "-") + ".jar")
        importJvmMod(repo, modFile)
        return modFile
    }

    override fun mapLocalFile(project: Project, currentDir: File): File =
        currentDir.resolve(repo.replace("/", "-") + ".jar")
}

data class GitHubPlainMod(
    var repo: String, var branch: String = "",
) : IMod {
    override fun resolveFile(project: Project, currentDir: File): File {
        val jsonText = URL("https://api.github.com/repos/$repo").readText()
        val json = Jval.read(jsonText)
        val branch = branch.ifBlank { json.getString("default_branch") }
        val modFile = currentDir.resolve(repo.replace("/", "-") + ".zip")
        importPlainMod(repo, branch, modFile)
        return modFile
    }

    override fun mapLocalFile(project: Project, currentDir: File): File =
        currentDir.resolve(repo.replace("/", "-") + ".zip")

    infix fun branch(branch: String) {
        this.branch = branch
    }
}

data class TaskMod(
    var task: String,
) : IMod {
    override fun preProcess(resolveMods: ResolveMods) {
        resolveMods.dependsOn(task)
    }

    val casualName = task.replace(":", "-").trim('-')
    override fun resolveFile(project: Project, currentDir: File): File {
        val sourceTask = project.tasks.getByPath(task)
        val output = sourceTask.outputs.files.singleFile
        return currentDir.resolve("FromTask-$casualName.zip").apply {
            output.copyTo(this, overwrite = true)
        }
    }

    override fun mapLocalFile(project: Project, currentDir: File): File {
        return currentDir.resolve("FromTask-$casualName.zip")
    }
}