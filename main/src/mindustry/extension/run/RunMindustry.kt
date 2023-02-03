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
     */
    inline fun addClient(config: AddClientSpec.() -> Unit) {
        val client = Client()
        client.modpack = defaultModpackName
        AddClientSpec(proj, client).config()
        clients.add(client)
    }
    /**
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
    fun addClient(config: Action<AddClientSpec>) {
        addClient {
            config.execute(this)
        }
    }
    /**
     * ### Kotlin DSL
     * ```kotlin
     * addServer {
     *    name = "" // optional
     *    official(version="v141")
     * }
     * addServer {
     *    be latest
     * }
     */
    inline fun addServer(config: AddServerSpec.() -> Unit) {
        val server = Server()
        server.modpack = defaultModpackName
        AddServerSpec(proj, server).config()
        servers.add(server)
    }
    /**
     * ### Groovy DSL
     * ```groovy
     * addServer {
     *    name = "" // optional
     *    official version: "v141"
     * }
     * addServer {
     *    be version: latest
     * }
     * ```
     */
    fun addServer(config: Action<AddServerSpec>) {
        addServer {
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
    inline fun addModpack(name: String = defaultModpackName, config: AddModpackSpec.() -> Unit) {
        val modpackName = formatValidGradleName(name)
        if (modpackName.isBlank()) {
            proj.logger.warn(
                "Modpack's name can't be blank, but \"$name\" is given. Any character other than [a-zA-Z0-9] will be ignored."
            )
            return
        }
        val modpack = Modpack(modpackName)
        AddModpackSpec(proj, modpack).config()
        if (modpack.mods.isEmpty()) {
            proj.logger.warn("Modpack<$modpackName> doesn't contains any mod, and it will be ignored.")
            return
        }
        modpacks.add(modpack)
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
    fun addModpack(name: String, config: Action<AddModpackSpec>) {
        addModpack(name) {
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
    fun addModpack(config: Action<AddModpackSpec>) {
        addModpack(defaultModpackName, config)
    }
}

fun RunMindustryExtension.findModpackByName(name: String?): Modpack? {
    if (name == null) return null
    return modpacks.find { it.name == name }
}