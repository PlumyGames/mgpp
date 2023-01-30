@file:JvmMultifileClass
@file:JvmName("ExtensionKt")
@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.mindustry.*
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

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
    val proj: Project,
) {
    val clients = ArrayList<Client>()
    val servers = ArrayList<Server>()
    val modpacks = ArrayList<Modpack>()
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
        client.modpack = "default"
        AddClientSpec(proj, client).config()
        clients.add(client)
    }

    fun addClient(config: Action<AddClientSpec>) {
        val client = Client()
        client.modpack = "default"
        config.execute(AddClientSpec(proj, client))
        clients.add(client)
    }

    inline fun addServer(config: AddServerSpec.() -> Unit) {
        val server = Server()
        server.modpack = "default"
        AddServerSpec(proj, server).config()
        servers.add(server)
    }

    fun addServer(config: Action<AddServerSpec>) {
        val server = Server()
        server.modpack = "default"
        config.execute(AddServerSpec(proj, server))
        servers.add(server)
    }

    inline fun addModpack(name: String = "default", config: AddModpackSpec.() -> Unit) {
        val modpack = Modpack(name)
        AddModpackSpec(proj, modpack).config()
        if (modpack.name.isNotBlank()) {
            modpacks.add(modpack)
        }
    }

    fun addModpack(name: String, config: Action<AddModpackSpec>) {
        val modpack = Modpack(name)
        config.execute(AddModpackSpec(proj, modpack))
        if (modpack.name.isNotBlank()) {
            modpacks.add(modpack)
        }
    }

    fun addModpack(config: Action<AddModpackSpec>) {
        addModpack("default", config)
    }
}
