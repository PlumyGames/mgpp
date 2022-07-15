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
/**
 * The main extension of [Mgpp].
 * It provides many configurations for Mindustry modding development:
 * - [meta]: the `mod.(h)json` that will be included in the `:jar` task.
 * You can modify this, but it only affects the output file.
 */
open class MindustryExtension(
    target: Project,
) {
    /**
     * @see ProjectType.Mod
     */
    @JvmField
    val Mod = ProjectType.Mod
    /**
     * @see ProjectType.Plugin
     */
    @JvmField
    val Plugin = ProjectType.Plugin
    /**
     * The check time(sec) for latest version.
     *
     * 1 hour as default.
     */
    var outOfDateTime: Int
        get() = (Mgpp.outOfDataTime / 1000).toInt()
        set(value) {
            Mgpp.outOfDataTime = value * 1000L
        }
    val _projectType = target.prop<ProjectType>().apply {
        convention(ProjectType.Mod)
    }
    /**
     * The project type will influence dependency resolution.
     */
    var projectType: ProjectType
        get() = _projectType.getOrElse(ProjectType.Mod)
        set(value) {
            _projectType.set(value)
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
    @DisableIfWithout("java")
    val _deploy = DeploySpec(target)
    /**
     * Configure how to deploy your artifacts.
     */
    @DisableIfWithout("java")
    fun deploy(func: Action<DeploySpec>) {
        func.execute(_deploy)
    }
    /**
     * Configure how to deploy your artifacts.
     */
    @DisableIfWithout("java")
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
        keepOutlines: Boolean = ModMeta.default("keepOutlines"),
    ) = io.github.liplum.mindustry.ModMeta(
        name = name,
        displayName = displayName,
        author = author,
        description = description,
        subtitle = subtitle,
        version = version,
        main = main,
        minGameVersion = minGameVersion,
        repo = repo,
        dependencies = dependencies,
        hidden = hidden,
        java = java,
        keepOutlines = keepOutlines
    )
}