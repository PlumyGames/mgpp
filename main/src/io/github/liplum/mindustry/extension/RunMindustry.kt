@file:JvmMultifileClass
@file:JvmName("ExtensionKt")
@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.mindustry.*
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.ExtensionAware
import java.io.File

/**
 * Retrieves the `runMindustry`: [RunMindustryExtension] extension.
 */
val Project.`runMindustry`: RunMindustryExtension
    get() = (this as ExtensionAware).extensions.getByName(R.x.runMindustry) as RunMindustryExtension
/**
 * Configures the [mindustry][RunMindustryExtension] extension.
 */
fun Project.`runMindustry`(configure: Action<RunMindustryExtension>): Unit =
    (this as ExtensionAware).extensions.configure(R.x.runMindustry, configure)
/**
 * [runMindustry] is used to create [runClient] and [runServer] tasks dynamically after build script is evaluated.
 *
 * Therefore, you can simply ignore `runMindustry` if you don't want to run the game.
 */
open class RunMindustryExtension(
    val target: Project,
) {
    val clients = ArrayList<Client>()
    val servers = ArrayList<Server>()
    /**
     * ### Kotlin DSL
     * ```kotlin
     * addClient {
     *    name = "" // optional
     *    official(version="v141")
     * }
     * addClient {
     *    be latest
     * }
     * addClient {
     *    github(
     *        user="mindustry-antigrief"
     *        repo="mindustry-client"
     *        tag = "v8.0.0",
     *        file = "erekir-client.jar",
     *    )
     * }
     * addClient {
     *    fooClient(
     *       tag = "v8.0.0",
     *       file = "erekir-client.jar",
     *    )
     * }
     * ```
     * ### Groovy DSL
     * ```groovy
     * addClient {
     *    name = "" // optional
     *    official version: "v141"
     * }
     * addClient {
     *    be version: latest
     * }
     * addClient {
     *    github(
     *        user: "mindustry-antigrief"
     *        repo: "mindustry-client"
     *        tag: "v8.0.0",
     *        file: "erekir-client.jar",
     *    )
     * }
     * addClient {
     *    fooClient(
     *       tag: "v8.0.0",
     *       file: "erekir-client.jar",
     *    )
     * }
     * ```
     */
    inline fun addClient(config: AddClientSpec.() -> Unit) {
        val client = Client()
        AddClientSpec(target, client).config()
        clients.add(client)
    }

    fun addClient(config: Action<AddClientSpec>) {
        val client = Client()
        config.execute(AddClientSpec(target, client))
        clients.add(client)
    }

    inline fun addServer(config: AddServerSpec.() -> Unit) {
        val server = Server()
        AddServerSpec(target, server).config()
        servers.add(server)
    }

    fun addServer(config: Action<AddServerSpec>) {
        val server = Server()
        config.execute(AddServerSpec(target, server))
        servers.add(server)
    }

}

//<editor-fold desc="Common">
open class Common {
    /** @see [AddClientSpec.name] */
    var name: String = ""
    /** @see [AddClientSpec.startupArgs] */
    val startupArgs = ArrayList<String>()
    /** @see [AddClientSpec.jvmArgs] */
    val jvmArgs = ArrayList<String>()
    var location: IGameLoc? = null
}

abstract class AddCommonSpec<T : Common> {
    protected abstract val proj: Project
    protected abstract val backend: T
    val latest: Notation get() = Notation.latest
    /**
     * *Optional*
     * An empty String as default.
     * It affects gradle task names.
     * ```
     * runClient // if it's empty
     * runClient2 // if second name is still empty
     * runClientFooClient // if [name] is "FooClient"
     * ```
     */
    var name: Any
        get() = backend.name
        set(value) {
            backend.name = value.toString().replace(" ", "")
        }
    val startupArgs get() = backend.startupArgs
    /**
     * The arguments of JVM.
     *
     * Because Mindustry desktop is based on Lwjgl3, the `-XstartOnFirstThread` will be passed when run on macOS.
     */
    val jvmArgs get() = backend.jvmArgs
    fun github(
        user: String,
        repo: String,
        tag: String,
        file: String,
    ) {
        backend.location = GitHubGameLoc(
            user = user,
            repo = repo,
            tag = tag,
            file = file,
        )
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
     * ```kotlin
     * official(version="v141")
     * ```
     */
    abstract fun official(version: String)
    /**
     * ```kotlin
     * official(version=latest)
     * ```
     */
    abstract fun official(version: Notation)
    /**
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

    abstract fun be(version: String)
    abstract fun be(version: Notation)

    fun be(props: Map<String, Any>) {
        when (val version = props["version"]?.toString()) {
            Notation.latest.toString() -> be(version = latest)
            null -> proj.logger.log(LogLevel.WARN, "No \"version\" given in be(Map<String,Any>)")
            else -> be(version)
        }
    }

    fun fromLocalDisk(path: String) {
        backend.location = LocalGameLoc(File(path))
    }

    fun fromLocalDisk(file: File) {
        backend.location = LocalGameLoc(file)
    }

    fun fromLocalDisk(props: Map<String, Any>) {
        val path = props["path"]
        val file = props["file"]
        if (path != null) {
            backend.location = LocalGameLoc(File(path as String))
        } else if (file != null) {
            backend.location = LocalGameLoc(file as File)
        } else {
            proj.logger.log(
                LogLevel.WARN,
                "Neither \"path\" nor \"file\" given in fromLocalDisk(Map<String,Any>)"
            )
        }
    }


}
//</editor-fold>

//<editor-fold desc="Add Client Spec">
class Client : Common() {
    /** @see [AddClientSpec.dataDir] */
    var dataDir: String? = null
}

class AddClientSpec(
    override val proj: Project,
    override val backend: Client,
) : AddCommonSpec<Client>() {
    /**
     * *Optional*
     * The name of Mindustry's data directory where to put saves.
     *
     * The default [dataDir] is the same as [name].
     */
    var dataDir: String?
        get() = backend.dataDir
        set(value) {
            backend.dataDir = value
        }

    override fun official(version: String) {
        github(
            user = R.anuken,
            repo = R.mindustry,
            tag = version,
            file = R.officialRelease.client,
        )
    }

    override fun official(version: Notation) {
        when (version) {
            Notation.latest -> backend.location = LatestOfficialMindustryLoc(file = R.officialRelease.client)
            else -> proj.logger.log(LogLevel.WARN, "Version $version is unsupported")
        }
    }

    override fun be(version: String) {
        github(
            user = R.anuken,
            repo = R.mindustryBuilds,
            tag = version,
            file = "Mindustry-BE-Desktop-$version.jar",
        )
    }

    override fun be(version: Notation) {
        when (version) {
            Notation.latest -> backend.location = LatestBeMindustryLoc(file = "Mindustry-BE-Desktop-$version.jar")
            else -> proj.logger.log(LogLevel.WARN, "Version $version is unsupported")
        }
    }

    fun fooClient(
        tag: String,
        file: String,
    ) {
        github(
            user = R.fooClient.user,
            repo = R.fooClient.repo,
            tag = tag,
            file = file,
        )
    }

    fun fooClient(props: Map<String, String>) {
        fooClient(
            tag = props["tag"] ?: "",
            file = props["file"] ?: "",
        )
    }
}
//</editor-fold>

//<editor-fold desc="Add Server Spec">
class Server : Common() {
}

class AddServerSpec(
    override val proj: Project,
    override val backend: Server
) : AddCommonSpec<Server>() {

    override fun official(version: String) {
        github(
            user = R.anuken,
            repo = R.mindustry,
            tag = version,
            file = R.officialRelease.server,
        )
    }

    override fun official(version: Notation) {
        when (version) {
            Notation.latest -> backend.location = LatestOfficialMindustryLoc(file = R.officialRelease.server)
            else -> proj.logger.log(LogLevel.WARN, "Version $version is unsupported")
        }
    }

    override fun be(version: String) {
        github(
            user = R.anuken,
            repo = R.mindustryBuilds,
            tag = version,
            file = "Mindustry-BE-Server-$version.jar",
        )
    }

    override fun be(version: Notation) {
        when (version) {
            Notation.latest -> backend.location = LatestBeMindustryLoc(file = "Mindustry-BE-Server-$version.jar")
            else -> proj.logger.log(LogLevel.WARN, "Version $version is unsupported")
        }
    }
}
//</editor-fold>
