@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.plusAssign
import org.gradle.api.Action
import org.gradle.api.Project
import java.io.File
import java.net.URL

enum class GameSideType(
    val gradleName: String
) {
    Client("client"),
    Server("server")
}

open class GameSide(
    override val name: String,
    override val isAnonymous: Boolean,
    val type: GameSideType,
) : NamedModel {
    /** @see [AddGameSideSpec.startupArgs] */
    val startupArgs = ArrayList<String>()

    /** @see [AddGameSideSpec.jvmArgs] */
    val jvmArgs = ArrayList<String>()

    /** @see [AddClientSpec.dataDir] */
    var dataDir: IDataDirLoc? = null
    var location: IGameLoc? = null
    var modpack: String? = null
    val gradleName get() = normalizeName4Gradle(name)
    fun localPropKey(key: String): String {
        val gradleName = gradleName
        return if (gradleName.isBlank())
            "mgpp.${type.gradleName}.${key}"
        else
            "mgpp.${type.gradleName}.${gradleName}.${key}"
    }

    val locationLocalPropKey get() = localPropKey("game")
    val dataDirLocalPropKey get() = localPropKey("dataDir")
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

    fun useModpack(name: String) {
        modpack = normalizeName4Gradle(name)
        proj.logger.info("Modpack<$name> was used in game<${name}>.")
    }

    fun useModpack(modpack: Modpack) {
        useModpack(modpack.name)
    }

    fun useModpack(
        name: String = "",
        config: AddModpackSpec.() -> Unit
    ) {
        useModpack(proj.runMindustry.addModpack(name, config))
    }

    fun useModpack(
        name: String,
        config: Action<AddModpackSpec>
    ) {
        useModpack(proj.runMindustry.addModpack(name, config))
    }

    fun useModpack(
        config: Action<AddModpackSpec>
    ) {
        useModpack(proj.runMindustry.addModpack("", config))
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

    fun url(url: URL) {
        UrlGameLoc(url).checkAndSet()
    }

    fun url(url: String) {
        this.url(URL(url))
    }

    fun localFile(path: String) {
        this.localFile(File(path))
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
}

/**
 * Convert strings to a valid gradle task name, refer to [NameRule.Camel].
 * For examples:
 * - "I'm invalid name" -> "IMInvalidName"
 */
fun normalizeName4Gradle(raw: String): String {
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