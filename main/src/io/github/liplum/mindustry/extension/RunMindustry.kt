@file:JvmMultifileClass
@file:JvmName("ExtensionKt")

package io.github.liplum.mindustry.extension

import io.github.liplum.mindustry.*
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import java.io.File

/**
 * ## How to use
 * ```kotlin
 * runMindustry {
 *   //...
 * }
 * ```
 * ```groovy
 * runMindustry {
 *   //...
 * }
 * ```
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

//<editor-fold desc="Add Client Spec">
class Client {
    /** @see [AddClientSpec.name] */
    var name: String = ""
    /** @see [AddClientSpec.startupArgs] */
    val startupArgs = ArrayList<String>()
    /** @see [AddClientSpec.jvmArgs] */
    val jvmArgs = ArrayList<String>()
    /** @see [AddClientSpec.dataDir] */
    var dataDir: String? = null
    var location: IGameLoc? = null
}

class AddClientSpec(
    private val proj: Project,
    private val client: Client
) {
    val latest: Notation get() = Notation.latest
    /**
     * *Optional*
     * An empty String as default.
     * It will affect the name of gradle task.
     * ```
     * runClient // if it's empty
     * runClient2 // if second name is still empty
     * runClientFooClient // if [name] is "FooClient"
     * ```
     */
    var name: Any
        get() = client.name
        set(value) {
            client.name = value.toString().replace(" ", "")
        }
    /**
     * *Optional*
     * The name of Mindustry's data directory where to put saves.
     *
     * The default [dataDir] is the same as [name].
     */
    var dataDir: String?
        get() = client.dataDir
        set(value) {
            client.dataDir = value
        }
    val startupArgs get() = client.startupArgs
    /**
     * The arguments of JVM.
     *
     * Because of Lwjgl3, the `-XstartOnFirstThread` will be passed when run on macOS.
     */
    val jvmArgs get() = client.jvmArgs
    fun github(
        user: String,
        repo: String,
        tag: String,
        file: String,
    ) {
        client.location = GitHubGameLoc(
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
    fun official(version: String) {
        github(
            user = R.anuken,
            repo = R.mindustry,
            tag = version,
            file = R.MindustryClientReleaseFileName,
        )
    }
    /**
     * ```kotlin
     * official(version=latest)
     * ```
     */
    fun official(version: Notation) {
        when (version) {
            Notation.latest -> client.location = LatestOfficialMindustryLoc(file = R.MindustryClientReleaseFileName)
            else -> proj.logger.log(LogLevel.WARN, "Version $version is unsupported")
        }
    }
    /**
     * ```groovy
     * official version: "v141"
     * official version: latest
     * ```
     */
    fun official(props: Map<String, Any>) {
        when (val version = props["version"]?.toString()) {
            Notation.latest.toString() -> official(version = latest)
            null -> proj.logger.log(LogLevel.WARN, "No \"version\" given in addClient.official(Map<String,Any>)")
            else -> official(version)
        }
    }

    fun be(version: String) {
        github(
            user = R.anuken,
            repo = R.mindustryBuilds,
            tag = version,
            file = "Mindustry-BE-Desktop-$version.jar",
        )
    }

    fun be(props: Map<String, Any>) {
        when (val version = props["version"]?.toString()) {
            Notation.latest.toString() -> be(version = latest)
            null -> proj.logger.log(LogLevel.WARN, "No \"version\" given in addClient.be(Map<String,Any>)")
            else -> be(version)
        }
    }

    fun be(version: Notation) {
        when (version) {
            Notation.latest -> client.location = LatestBeMindustryLoc(file = "Mindustry-BE-Desktop-$version.jar")
            else -> proj.logger.log(LogLevel.WARN, "Version $version is unsupported")
        }
    }

    fun fromLocalDisk(path: String) {
        client.location = LocalGameLoc(File(path))
    }

    fun fromLocalDisk(file: File) {
        client.location = LocalGameLoc(file)
    }

    fun fromLocalDisk(props: Map<String, Any>) {
        val path = props["path"]
        val file = props["file"]
        if (path != null) {
            client.location = LocalGameLoc(File(path as String))
        } else if (file != null) {
            client.location = LocalGameLoc(file as File)
        } else {
            proj.logger.log(
                LogLevel.WARN,
                "Neither \"path\" nor \"file\" given in addClient.fromLocalDisk(Map<String,Any>)"
            )
        }
    }

    fun fooClient(
        tag: String,
        file: String,
    ) {
        github(
            user = "mindustry-antigrief",
            repo = "mindustry-client",
            tag = tag,
            file = file,
        )
    }

    fun fooClient(props: Map<String, String>) {
        github(
            user = "mindustry-antigrief",
            repo = "mindustry-client",
            tag = props["tag"] ?: "",
            file = props["file"] ?: "",
        )
    }
}
//</editor-fold>

//<editor-fold desc="Add Server Spec">
class Server {
    /** @see [AddServerSpec.name] */
    var name: String = ""
    /** @see [AddServerSpec.startupArgs] */
    val startupArgs = ArrayList<String>()
    /** @see [AddServerSpec.jvmArgs] */
    val jvmArgs = ArrayList<String>()
    var location: IGameLoc? = null
}

class AddServerSpec(
    private val proj: Project,
    private val server: Server
) {
    val latest: Notation get() = Notation.latest
    /**
     * *Optional*
     * An empty String as default.
     * It will affect the name of gradle task.
     * ```
     * runServer // if it's empty
     * runServer2 // if second name is still empty
     * ```
     */
    var name: Any
        get() = server.name
        set(value) {
            server.name = value.toString().replace(" ", "")
        }
    val startupArgs get() = server.startupArgs
    /**
     * The arguments of JVM.
     */
    val jvmArgs get() = server.jvmArgs
    fun github(
        user: String,
        repo: String,
        tag: String,
        file: String,
    ) {
        server.location = GitHubGameLoc(
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
    fun official(version: String) {
        github(
            user = R.anuken,
            repo = R.mindustry,
            tag = version,
            file = R.MindustryClientReleaseFileName,
        )
    }
    /**
     * ```kotlin
     * official(version=latest)
     * ```
     */
    fun official(version: Notation) {
        when (version) {
            Notation.latest -> server.location = LatestOfficialMindustryLoc(file = R.MindustryServerReleaseFileName)
            else -> proj.logger.log(LogLevel.WARN, "Version $version is unsupported")
        }
    }
    /**
     * ```groovy
     * official version: "v141"
     * official version: latest
     * ```
     */
    fun official(props: Map<String, String>) {
        when (val version = props["version"]) {
            Notation.latest.toString() -> official(version = latest)
            null -> proj.logger.log(LogLevel.WARN, "No \"version\" given in AddServer.official(Map<String,Any>)")
            else -> official(version)
        }
    }

    fun be(version: String) {
        github(
            user = R.anuken,
            repo = R.mindustryBuilds,
            tag = version,
            file = "Mindustry-BE-Server-$version.jar",
        )
    }

    fun be(props: Map<String, Any>) {
        when (val version = props["version"]?.toString()) {
            Notation.latest.toString() -> be(version = latest)
            null -> proj.logger.log(LogLevel.WARN, "No \"version\" given in AddServer.be(Map<String,Any>)")
            else -> be(version)
        }
    }

    fun be(version: Notation) {
        when (version) {
            Notation.latest -> server.location = LatestBeMindustryLoc(file = "Mindustry-BE-Server-$version.jar")
            else -> proj.logger.log(LogLevel.WARN, "Version $version is unsupported")
        }
    }

    fun fromLocalDisk(path: String) {
        server.location = LocalGameLoc(File(path))
    }

    fun fromLocalDisk(file: File) {
        server.location = LocalGameLoc(file)
    }

    fun fromLocalDisk(props: Map<String, Any>) {
        val path = props["path"]
        val file = props["file"]
        if (path != null) {
            server.location = LocalGameLoc(File(path as String))
        } else if (file != null) {
            server.location = LocalGameLoc(file as File)
        } else {
            proj.logger.log(
                LogLevel.WARN,
                "Neither \"path\" nor \"file\" given in AddServer.fromLocalDisk(Map<String,Any>)"
            )
        }
    }
}
//</editor-fold>
