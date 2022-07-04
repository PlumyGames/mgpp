@file:Suppress("RemoveRedundantBackticks")
@file:JvmName("Extension")

package io.github.liplum.mindustry

import io.github.liplum.dsl.findFileInOrder
import io.github.liplum.dsl.proDir
import io.github.liplum.dsl.prop
import io.github.liplum.dsl.rootDir
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

/**
 * Retrieves the [mindustry][MindustryExtension] extension.
 */
val Project.`mindustry`: MindustryExtension
    get() = (this as ExtensionAware).extensions.getByName(Mgpp.MainExtensionName) as MindustryExtension
/**
 * Configures the [mindustry][MindustryExtension] extension.
 */
fun Project.`mindustry`(configure: Action<MindustryExtension>): Unit =
    (this as ExtensionAware).extensions.configure(Mgpp.MainExtensionName, configure)
/**
 * The project type.
 */
enum class ProjectType {
    /**
     * For a mod, it will import all essential dependencies of Mindustry.
     */
    Mod,
    /**
     * For a plugin, it will only import server-related dependencies of Mindustry.
     */
    Plugin
}

interface IMgppNotation
object LatestNotation : IMgppNotation {
    override fun toString() = "latest"
}
/**
 * The main extension of [Mgpp].
 * It provides many configurations for Mindustry modding development:
 * - [meta]: the `mod.(h)json` that will be included in the `:jar` task.
 * You can modify this, but it only affects the output file.
 */
open class MindustryExtension(
    target: Project,
) {
    @JvmField
    val Mod = ProjectType.Mod
    @JvmField
    val Plugin = ProjectType.Plugin
    /**
     * The project type will influence dependency resolution.
     */
    val projectType = target.prop<ProjectType>().apply {
        convention(ProjectType.Mod)
    }
    @JvmField
    val _dependency = DependencySpec(target)
    /**
     * Configure the mindustry and arc dependency.
     * You should call [mindustryRepo] and [importMindustry] to apply the configuration.
     */
    fun dependency(func: Action<DependencySpec>) {
        func.execute(_dependency)
    }
    /**
     * Configure the mindustry and arc dependency.
     * You should call [mindustryRepo] and [importMindustry] to apply the configuration.
     */
    inline fun dependency(func: DependencySpec.() -> Unit) {
        _dependency.func()
    }
    @JvmField
    val _client = ClientSpec(target)
    /**
     * Configure the client to run and debug your mod on
     */
    fun client(func: Action<ClientSpec>) {
        func.execute(_client)
    }
    /**
     * Configure the client to run and debug your mod on
     */
    inline fun client(func: ClientSpec.() -> Unit) {
        _client.func()
    }
    @JvmField
    val _server = ServerSpec(target)
    /**
     * Configure the server to run and debug your mod on
     */
    fun server(func: Action<ServerSpec>) {
        func.execute(_server)
    }
    /**
     * Configure the server to run and debug your mod on
     */
    inline fun server(func: ServerSpec.() -> Unit) {
        _server.func()
    }
    @JvmField
    val _mods = ModsSpec(target)
    /**
     * Configure what mod you want to work with.
     */
    fun mods(func: Action<ModsSpec>) {
        func.execute(_mods)
    }
    /**
     * Configure what mod you want to work with.
     */
    inline fun mods(func: ModsSpec.() -> Unit) {
        _mods.func()
    }
    @JvmField
    val _run = RunSpec(target)
    /**
     * Configure how to run and debug the game.
     */
    fun run(func: Action<RunSpec>) {
        func.execute(_run)
    }
    /**
     * Configure how to run and debug the game.
     */
    inline fun run(func: RunSpec.() -> Unit) {
        _run.func()
    }
    @JvmField
    val _deploy = DeploySpec(target)
    /**
     * Configure how to deploy your artifacts.
     */
    fun deploy(func: Action<DeploySpec>) {
        func.execute(_deploy)
    }
    /**
     * Configure how to deploy your artifacts.
     */
    inline fun deploy(func: DeploySpec.() -> Unit) {
        _deploy.func()
    }
    @JvmField
    val _modMeta = target.prop<ModMeta>().apply {
        target.run {
            convention(
                ModMeta.fromHjson(
                    findFileInOrder(
                        proDir("mod.hjson"),
                        proDir("mod.json"),
                        rootDir("mod.hjson"),
                        rootDir("mod.json")
                    )
                )
            )
        }
    }
    /**
     * Configure `mod.hjson` for output purpose.
     * It will automatically fetch the `mod.(h)json` from the following paths in order:
     * 1. [Project.getProjectDir]/mod.hjson
     * 2. [Project.getProjectDir]/mod.json
     * 3. [Project.getRootProject]/mod.hjson
     * 4. [Project.getRootProject]/mod.json
     */
    var meta: ModMeta
        get() = _modMeta.get()
        set(value) {
            _modMeta.set(value)
        }
    /**
     * @see [io.github.liplum.mindustry.ModMeta]
     */
    fun ModMeta(info: Map<String, Any>) =
        io.github.liplum.mindustry.ModMeta(info)
    /**
     * @see [io.github.liplum.mindustry.ModMeta]
     */
    fun ModMeta(
        name: String = ModMeta.default("name"),
        displayName: String = ModMeta.default("displayName"),
        author: String = ModMeta.default("author"),
        description: String = ModMeta.default("description"),
        subtitle: String = ModMeta.default("subtitle"),
        version: String = ModMeta.default("version"),
        main: String = ModMeta.default("main"),
        minGameVersion: String = ModMeta.default("minGameVersion"),
        repo: String = ModMeta.default("repo"),
        dependencies: List<String> = ModMeta.default("dependencies"),
        hidden: Boolean = ModMeta.default("hidden"),
        java: Boolean = ModMeta.default("java"),
        hideBrowser: Boolean = ModMeta.default("hideBrowser"),
    ) = io.github.liplum.mindustry.ModMeta(
        name,
        displayName,
        author,
        description,
        subtitle,
        version,
        main,
        minGameVersion,
        repo,
        dependencies,
        hidden,
        java,
        hideBrowser
    )
}