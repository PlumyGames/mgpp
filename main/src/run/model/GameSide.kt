@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.plusAssign
import io.github.liplum.mindustry.LocalProperties.local
import io.github.liplum.mindustry.RunMindustryExtension.Companion.defaultModpackName
import io.github.liplum.mindustry.run.model.NamedModel
import org.gradle.api.Project
import java.io.File

open class GameSide(
    override val name: String,
    override val isAnonymous: Boolean
) : NamedModel {
    /** @see [AddGameSideSpec.startupArgs] */
    val startupArgs = ArrayList<String>()

    /** @see [AddGameSideSpec.jvmArgs] */
    val jvmArgs = ArrayList<String>()

    /** @see [AddClientSpec.dataDir] */
    var dataDir: IDataDirLoc? = null
    var location: IGameLoc? = null
    var modpack: String? = null
}

abstract class AddGameSideSpec<T : GameSide> {
    protected abstract val proj: Project
    protected abstract val backend: T
    val latest: Notation get() = Notation.latest
    val startupArgs get() = backend.startupArgs

    /**
     * The arguments of JVM.
     *
     * Because Mindustry desktop is based on Lwjgl3, the `-XstartOnFirstThread` will be passed when run on macOS.
     */
    val jvmArgs get() = backend.jvmArgs

    /**
     * *Optional*
     *
     * Where Mindustry's data directory to put saves.
     * The default [dataDir] is the same as [name].
     *
     *  ## Default data directory of Mindustry desktop
     * - Linux: `$HOME/.local/share/Mindustry/`
     * - MacOS: `$HOME/Library/Application Support/Mindustry/`
     * - Windows: `%AppData%/Mindustry/`
     *
     *  ## Default data directory of Mindustry server
     *  `./config/`
     */
    var dataDir: IDataDirLoc?
        get() = backend.dataDir
        set(value) {
            backend.dataDir = value
        }

    fun putDataAt(props: Map<String, Any>) {
        val path = props["path"]
        val file = props["file"]
        if (path != null) {
            putDataAt(path = path.toString())
        } else if (file != null) {
            putDataAt(file = proj.project.file(file))
        } else {
            proj.logger.error(
                "Neither \"path\" nor \"file\" given in putDataAt(Map<String,Any>)"
            )
        }
    }

    fun putDataAt(path: String) {
        dataDir = LocalDataDirLoc(File(path))
    }

    fun putDataAt(file: File) {
        dataDir = LocalDataDirLoc(file)
    }

    fun putDataAt(loc: IDataDirLoc) {
        dataDir = loc
    }

    var modpack: String?
        get() = backend.modpack
        set(value) {
            backend.modpack = value
        }

    fun useModpack(props: Map<String, String>) {
        useModpack(
            name = props["name"] ?: defaultModpackName,
        )
    }

    fun useModpack(name: String) {
        modpack = formatValidGradleName(name)
    }

    fun useModpack(modpack: Modpack) {
        this.modpack = modpack.name
    }

    protected fun IGameLoc.checkAndSet() {
        if (backend.location != null) {
            proj.logger.warn("The game is already set to ${backend.location}, and will be overridden by $this.")
        }
        backend.location = this
    }

    fun github(
        user: String,
        repo: String,
        tag: String,
        file: String,
    ) {
        GitHubGameLoc(
            user = user,
            repo = repo,
            tag = tag,
            file = file,
        ).checkAndSet()
    }

    fun github(props: Map<String, String>) {
        github(
            user = props["user"] ?: "",
            repo = props["repo"] ?: "",
            tag = props["tag"] ?: "",
            file = props["file"] ?: "",
        )
    }

    /**
     * ### Kotlin DSL
     * ```kotlin
     * official(version="v141")
     * ```
     */
    abstract fun official(version: String)

    /**
     * ### Kotlin DSL
     * ```kotlin
     * official(version=latest)
     * ```
     */
    abstract fun official(version: Notation)

    /**
     * ### Groovy DSL
     * ```groovy
     * official version: "v141"
     * official version: latest
     * ```
     */
    fun official(props: Map<String, Any>) {
        when (val version = props["version"]?.toString()) {
            Notation.latest.toString() -> official(version = latest)
            null -> proj.logger.error("No \"version\" given in official(Map<String,Any>)")
            else -> official(version)
        }
    }

    /**
     * ### Kotlin DSL
     * ```kotlin
     * be(version="23786")
     * ```
     */
    abstract fun be(version: String)

    /**
     * ### Kotlin DSL
     * ```kotlin
     * be(version=23786)
     * ```
     */
    fun be(version: Int) {
        be(version = version.toString())
    }

    /**
     * ### Kotlin DSL
     * ```kotlin
     * be(version=latest)
     * ```
     */
    abstract fun be(version: Notation)

    /**
     * ### Groovy DSL
     * ```groovy
     * be version: latest
     * be version: 23786
     * be version: "23786"
     * ```
     */
    fun be(props: Map<String, Any>) {
        when (val version = props["version"]?.toString()) {
            Notation.latest.toString() -> be(version = latest)
            null -> proj.logger.error("No \"version\" given in be(Map<String,Any>)")
            else -> be(version)
        }
    }

    fun localFile(path: String) {
        LocalGameLoc(File(path)).checkAndSet()
    }

    fun localFile(file: File) {
        LocalGameLoc(file).checkAndSet()
    }

    fun localFile(props: Map<String, Any>) {
        val path = props["path"]
        val file = props["file"]
        if (path != null) {
            localFile(path = path.toString())
        } else if (file != null) {
            localFile(file = file as File)
        } else {
            proj.logger.error(
                "Neither \"path\" nor \"file\" given in localFile(Map<String,Any>)"
            )
        }
    }

    fun localProperties(key: String) {
        val path = proj.local[key]
        if (path == null) {
            proj.logger.error("\"$key\" not found for \"${backend.name}\" in local proprieties.")
        } else {
            localFile(path = path)
        }
    }
}

fun formatValidGradleName(raw: String): String {
    val s = StringBuilder()
    var nextUpper = true
    for (c in raw) {
        if (c in '0'..'9' || c in 'a'..'z' || c in 'A'..'Z') {
            if (nextUpper) {
                s += c.uppercaseChar()
                nextUpper = false
            } else {
                s += c
            }
        } else {
            nextUpper = true
        }
    }
    return s.toString()
}