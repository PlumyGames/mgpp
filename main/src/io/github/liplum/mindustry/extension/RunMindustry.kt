@file:JvmMultifileClass
@file:JvmName("ExtensionKt")

package io.github.liplum.mindustry.extension

import io.github.liplum.mindustry.*
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
     *        version = "v8.0.0",
     *        release = "erekir-client.jar",
     *    )
     * }
     * addClient {
     *    fooClient(
     *       version = "v8.0.0",
     *       release = "erekir-client.jar",
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
     *        version: "v8.0.0",
     *        release: "erekir-client.jar",
     *    )
     * }
     * addClient {
     *    fooClient(
     *       version: "v8.0.0",
     *       release: "erekir-client.jar",
     *    )
     * }
     * ```
     */
    inline fun addClient(config: AddClientSpec.() -> Unit) {
        val client = Client()
        AddClientSpec(target, client).config()
        clients.add(client)
    }

    fun addServer() {

    }
}

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
     * runClient2 // if there are two emtpy names
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
        val loc = GitHubGameLoc(
            user = user,
            repo = repo,
            tag = tag,
            file = file,
        )
        client.location = loc
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
            Notation.latest -> client.location = LatestOfficialMindustryLoc()
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

    fun be(props: Map<String, String>) {
        be(
            version = props["version"] ?: "",
        )
    }

    fun fromLocalDisk(path: String) {
        val loc = LocalGameLoc(File(path))
        client.location = loc
    }

    fun fromLocalDisk(file: File) {
        val loc = LocalGameLoc(file)
        client.location = loc
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
}
