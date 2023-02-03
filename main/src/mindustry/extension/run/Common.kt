@file:JvmMultifileClass
@file:JvmName("ExtensionKt")
@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.plusAssign
import io.github.liplum.mindustry.*
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import java.io.File

open class Common(
    /**
     * *Optional*
     * An empty String as default.
     * It affects gradle task names.
     * ```
     * runClient // if it's anonymous
     * runClient2 // if second name is still anonymous
     * runClientFooClient // if [name] is "FooClient"
     * runServer // if it's anonymous
     * ```
     */
    val name: String,
    /**
     * Whether this is anonymous.
     */
    val isAnonymous: Boolean,
) {
    /** @see [AddCommonSpec.startupArgs] */
    val startupArgs = ArrayList<String>()
    /** @see [AddCommonSpec.jvmArgs] */
    val jvmArgs = ArrayList<String>()
    /** @see [AddClientSpec.dataDir] */
    var dataDir: IDataDirLoc = MindustryDefaultDataDirLoc
    var location: IGameLoc? = null
    var modpack: String? = null
}

abstract class AddCommonSpec<T : Common> {
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
    var dataDir: Any?
        get() = when (val dir = backend.dataDir) {
            is MindustryDefaultDataDirLoc -> null
            is ProjBuildDataDirLoc -> dir.name
            is LocalDataDirLoc -> dir.dir
            else -> null
        }
        set(value) {
            when (value) {
                is File -> backend.dataDir = LocalDataDirLoc(value)
                is String -> backend.dataDir = LocalDataDirLoc(File(value))
                is IDataDirLoc -> backend.dataDir = value
            }
        }
    var modpack: Any?
        get() = backend.modpack
        set(value) {
            backend.modpack = when (value) {
                is Modpack -> value.name
                null -> null
                else -> value.toString().let(::formatValidGradleName)
            }
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
            null -> proj.logger.log(LogLevel.WARN, "No \"version\" given in official(Map<String,Any>)")
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
            null -> proj.logger.log(LogLevel.WARN, "No \"version\" given in be(Map<String,Any>)")
            else -> be(version)
        }
    }

    fun fromLocalDisk(path: String) {
        LocalGameLoc(File(path)).checkAndSet()
    }

    fun fromLocalDisk(file: File) {
        LocalGameLoc(file).checkAndSet()
    }

    fun fromLocalDisk(props: Map<String, Any>) {
        val path = props["path"]
        val file = props["file"]
        if (path != null) {
            LocalGameLoc(File(path as String)).checkAndSet()
        } else if (file != null) {
            LocalGameLoc(file as File).checkAndSet()
        } else {
            proj.logger.log(
                LogLevel.WARN,
                "Neither \"path\" nor \"file\" given in fromLocalDisk(Map<String,Any>)"
            )
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