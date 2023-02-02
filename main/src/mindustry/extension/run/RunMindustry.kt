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
    companion object {
        const val defaultModpackName = "Default"
    }

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
        client.modpack = defaultModpackName
        AddClientSpec(proj, client).config()
        clients.add(client)
    }

    fun addClient(config: Action<AddClientSpec>) {
        addClient {
            config.execute(this)
        }
    }

    inline fun addServer(config: AddServerSpec.() -> Unit) {
        val server = Server()
        server.modpack = defaultModpackName
        AddServerSpec(proj, server).config()
        servers.add(server)
    }

    fun addServer(config: Action<AddServerSpec>) {
        addServer {
            config.execute(this)
        }
    }

    inline fun addModpack(name: String = defaultModpackName, config: AddModpackSpec.() -> Unit) {
        val modpack = Modpack(formatValidGradleName(name))
        AddModpackSpec(proj, modpack).config()
        if (modpack.name.isNotBlank() && modpack.mods.isNotEmpty()) {
            modpacks.add(modpack)
        }
    }

    fun addModpack(name: String, config: Action<AddModpackSpec>) {
        addModpack(name) {
            config.execute(this)
        }
    }

    fun addModpack(config: Action<AddModpackSpec>) {
        addModpack(defaultModpackName, config)
    }
}
