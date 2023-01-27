@file:JvmMultifileClass
@file:JvmName("ExtensionKt")

package io.github.liplum.mindustry.extension

import io.github.liplum.mindustry.GitHubGameLoc
import io.github.liplum.mindustry.IGameLoc
import io.github.liplum.mindustry.R
import org.gradle.api.Project

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
    target: Project,
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
        AddClientSpec(client).config()
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
@JvmInline
value class AddClientSpec(
    private val client: Client
) {
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

    fun official(version: String) {
        github(
            user = R.anuken,
            repo = R.mindustry,
            tag = version,
            file = R.MindustryClientReleaseFileName,
        )
    }

    fun official(props: Map<String, String>) {
        official(
            version = props["version"] ?: "",
        )
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
}
