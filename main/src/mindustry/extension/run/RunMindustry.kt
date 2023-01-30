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
