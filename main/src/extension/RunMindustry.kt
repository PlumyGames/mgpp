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
 * [runMindustry] is used to create `runClient` and `runServer` tasks dynamically after build script is evaluated.
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
     *    official(version="v141")
     * }
     * addClient("my name") {
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
     */
    inline fun addClient(
        name: String = "",
        config: AddClientSpec.() -> Unit
    ): Client {
        var clientName = formatValidGradleName(name)
        val isAnonymous = clientName.isBlank()
        if (isAnonymous) {
            val anonymousCount = clients.count { it.isAnonymous }
            clientName = if (anonymousCount == 0) ""
            else (anonymousCount + 1).toString()
        }
        val client = Client(name = clientName, isAnonymous = isAnonymous)
        client.modpack = defaultModpackName
        client.dataDir = ProjBuildDataDirLoc(
            namespace = "mindustryClientData",
            name = clientName.ifBlank { "Default" },
        )
        AddClientSpec(proj, client).config()
        clients.add(client)
        proj.logger.info("Client<$clientName> is added.", client)
        return client
    }
    /**
     * ### Groovy DSL
     * ```groovy
     * addClient {
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
    fun addClient(config: Action<AddClientSpec>): Client {
        return addClient {
            config.execute(this)
        }
    }
    /**
     * ### Groovy DSL
     * ```groovy
     * addClient("my name") {
     *    name = "" // optional
     *    official version: "v141"
     * }
     * ```
     */
    fun addClient(
        name: String,
        config: Action<AddClientSpec>
    ): Client {
        return addClient(name) {
            config.execute(this)
        }
    }
    /**
     * ### Kotlin DSL
     * ```kotlin
     * addServer("my server") {
     *    official(version="v141")
     * }
     * addServer {
     *    be latest
     * }
     */
    inline fun addServer(
        name: String = "",
        config: AddServerSpec.() -> Unit
    ): Server {
        var serverName = formatValidGradleName(name)
        val isAnonymous = serverName.isBlank()
        if (isAnonymous) {
            val anonymousCount = servers.count { it.isAnonymous }
            serverName = if (anonymousCount == 0) ""
            else (anonymousCount + 1).toString()
        }
        val server = Server(name = serverName, isAnonymous = isAnonymous)
        server.modpack = defaultModpackName
        server.dataDir = ProjBuildDataDirLoc(
            namespace = "mindustryServerData",
            name = serverName.ifBlank { "Default" },
        )
        AddServerSpec(proj, server).config()
        servers.add(server)
        proj.logger.info("Server<$serverName> is added.", server)
        return server
    }
    /**
     * ### Groovy DSL
     * ```groovy
     * addServer {
     *    official version: "v141"
     * }
     * addServer {
     *    be version: latest
     * }
     * ```
     */
    fun addServer(config: Action<AddServerSpec>): Server {
        return addServer {
            config.execute(this)
        }
    }
    /**
     * ### Groovy DSL
     * ```groovy
     * addServer("my name") {
     *    name = "" // optional
     *    official version: "v141"
     * }
     * ```
     */
    fun addServer(name: String, config: Action<AddServerSpec>): Server {
        return addServer(name) {
            config.execute(this)
        }
    }
    /**
     * ### Kotlin DSL
     * ```kotlin
     * addModpack { // add to default modpack
     *    jvm(repo="PlumyGames/mgpp")
     * }
     * addModpack("awesome mod") { //
     *    jvm(repo="PlumyGames/mgpp")
     *    github(repo="liplum/CyberIO") // auto-detect mod type, not recommended.
     * }
     */
    inline fun addModpack(
        name: String = defaultModpackName,
        config: AddModpackSpec.() -> Unit
    ): Modpack? {
        val modpackName = formatValidGradleName(name)
        if (modpackName.isBlank()) {
            proj.logger.warn(
                "Modpack's name Failed to be blank, but \"$name\" is given. Any character other than [a-zA-Z0-9] will be ignored."
            )
            return null
        }
        val modpack = Modpack(modpackName)
        AddModpackSpec(proj, modpack).config()
        if (modpack.isEmpty()) {
            proj.logger.warn("Modpack<$modpackName> doesn't contains any mod, and it will be ignored.")
            return null
        }
        modpacks.add(modpack)
        return modpack
    }
    /**
     * ### Groovy DSL
     * ```groovy
     * addModpack("awesome mod") { //
     *    jvm repo: "PlumyGames/mgpp"
     *    github repo: "liplum/CyberIO" // auto-detect mod type, not recommended.
     * }
     * ```
     */
    fun addModpack(
        name: String,
        config: Action<AddModpackSpec>
    ): Modpack? {
        return addModpack(name) {
            config.execute(this)
        }
    }
    /**
     * Add to default modpack
     * ```groovy
     * addModpack { //
     *    jvm repo: "PlumyGames/mgpp"
     *    github repo: "liplum/CyberIO" // auto-detect mod type, not recommended.
     * }
     * ```
     */
    fun addModpack(config: Action<AddModpackSpec>): Modpack? {
        return addModpack(defaultModpackName, config)
    }
}

fun RunMindustryExtension.findModpackByName(name: String?): Modpack? {
    if (name == null) return null
    return modpacks.find { it.name == name }
}
