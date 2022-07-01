@file:Suppress("RemoveRedundantBackticks")
@file:JvmName("Extension")

package io.github.liplum.mindustry

import io.github.liplum.dsl.listProp
import io.github.liplum.dsl.prop
import io.github.liplum.dsl.stringProp
import io.github.liplum.dsl.stringsProp
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPlugin

/**
 * Retrieves the [mindustry][MindustryExtension] extension.
 */
val Project.`mindustry`: MindustryExtension
    get() = (this as ExtensionAware).extensions.getByName(MindustryPlugin.MainExtensionName) as MindustryExtension
/**
 * Configures the [mindustry][MindustryExtension] extension.
 */
fun Project.`mindustry`(configure: Action<MindustryExtension>): Unit =
    (this as ExtensionAware).extensions.configure(MindustryPlugin.MainExtensionName, configure)
/**
 * Retrieves the [mindustry][MindustryExtension] extension.
 */
val Project.`mindustryAssets`: MindustryAssetsExtension
    get() = (this as ExtensionAware).extensions.getByName(MindustryPlugin.AssetExtensionName) as MindustryAssetsExtension
/**
 * Configures the [mindustry][MindustryExtension] extension.
 */
fun Project.`mindustryAssets`(configure: Action<MindustryAssetsExtension>): Unit =
    (this as ExtensionAware).extensions.configure(MindustryPlugin.AssetExtensionName, configure)

enum class ProjectType {
    Mod, Plugin
}

open class MindustryExtension(
    target: Project,
) {
    companion object {
        @JvmStatic
        val Mod = ProjectType.Mod
        @JvmStatic
        val Plugin = ProjectType.Plugin
    }
    /**
     * Configure the mindustry and arc dependency automatically.
     * This only works before [Project.dependencies] is called
     */
    val dependency = DependencySpec(target)
    /**
     * Configure the mindustry and arc dependency automatically.
     * This only works before [Project.dependencies] is called
     */
    fun dependency(func: Action<DependencySpec>) {
        func.execute(dependency)
    }
    /**
     * Configure the mindustry and arc dependency automatically.
     * This only works before [Project.dependencies] is called
     */
    inline fun dependency(func: DependencySpec.() -> Unit) {
        dependency.func()
    }

    val client = ClientSpec(target)
    fun client(func: Action<ClientSpec>) {
        func.execute(client)
    }

    inline fun client(func: ClientSpec.() -> Unit) {
        client.func()
    }

    val server = ServerSpec(target)
    fun server(func: Action<ServerSpec>) {
        func.execute(server)
    }

    inline fun server(func: ServerSpec.() -> Unit) {
        server.func()
    }

    val projectType = target.prop<ProjectType>().apply {
        convention(ProjectType.Mod)
    }
    val mods = ModsSpec(target)
    fun mods(func: Action<ModsSpec>) {
        func.execute(mods)
    }

    inline fun mods(func: ModsSpec.() -> Unit) {
        mods.func()
    }

    val run = RunSpec(target)
    fun run(func: Action<RunSpec>) {
        func.execute(run)
    }

    inline fun run(func: RunSpec.() -> Unit) {
        run.func()
    }

    val deploy = DeploySpec(target)
    fun deploy(func: Action<DeploySpec>) {
        func.execute(deploy)
    }

    inline fun deploy(func: DeploySpec.() -> Unit) {
        deploy.func()
    }

    val modMeta = target.prop<ModMeta>().apply {
        convention(ModMeta.fromHjson(
            target.rootDir.resolve("mod.hjson").let {
                if (it.exists()) it else target.rootDir.resolve("mod.json")
            }
        ))
    }
    var meta: ModMeta
        get() = modMeta.get()
        set(value) {
            modMeta.set(value)
        }

    fun addMeta(info: Map<String, Any>): ModMeta =
        modMeta.get().apply {
            this += ModMeta(info)
        }

    fun addMeta(meta: ModMeta): ModMeta =
        modMeta.get().apply {
            this += meta
        }
    /**
     * @see [io.github.liplum.mindustry.ModMeta]
     */
    fun modMeta(info: Map<String, Any>) =
        ModMeta(info).apply {
            modMeta.set(this)
        }

    fun modMeta(meta: ModMeta) =
        meta.apply {
            modMeta.set(this)
        }
    /**
     * @see [io.github.liplum.mindustry.ModMeta]
     */
    @JvmOverloads
    fun modMeta(
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
    ) = ModMeta(
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
    ).apply {
        modMeta.get() += this
    }
}

class DependencySpec(
    target: Project,
) {
    /**
     * Import v135 as default for now.
     * DO NOT trust this behavior, it may change later.
     */
    val arcDependency = target.prop<IDependency>().apply {
        convention(ArcDependency())
    }
    /**
     * Import v135 as default for now.
     * DO NOT trust this behavior, it may change later.
     */
    val mindustryDependency = target.prop<IDependency>().apply {
        convention(MindustryDependency())
    }
    val arc = ArcSpec()
    val mindustry = MindustrySpec()
    fun arc(version: String) {
        arcDependency.set(ArcDependency(version))
    }

    fun mindustry(version: String) {
        mindustryDependency.set(MindustryDependency(version))
    }

    fun mindustryMirror(version: String) {
        mindustryDependency.set(MirrorDependency(version))
    }

    fun arc(map: Map<String, Any>) {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified for `arc`")
        arcDependency.set(ArcDependency(version))
    }

    fun mindustry(map: Map<String, Any>) {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified for `mindustry`")
        mindustryDependency.set(MindustryDependency(version))
    }

    fun mindustryMirror(map: Map<String, Any>) {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified for `mindustryMirror`")
        mindustryDependency.set(MirrorDependency(version))
    }

    companion object {
        @JvmStatic
        val ArcRepo = MindustryPlugin.ArcJitpackRepo
        @JvmStatic
        val MindustryMirrorRepo = MindustryPlugin.MindustryJitpackMirrorRepo
        @JvmStatic
        val MindustryRepo = MindustryPlugin.MindustryJitpackRepo
        @JvmStatic
        fun ArcDependency(
            version: String = MindustryPlugin.DefaultMindustryVersion,
        ) = Dependency(MindustryPlugin.ArcJitpackRepo, version)
        @JvmStatic
        fun MindustryDependency(
            version: String = MindustryPlugin.DefaultMindustryVersion,
        ) = Dependency(MindustryPlugin.MindustryJitpackRepo, version)
        @JvmStatic
        fun Dependency(
            fullName: String = "",
            version: String = "",
        ) = io.github.liplum.mindustry.Dependency(fullName, version)
        @JvmStatic
        fun MirrorDependency(
            version: String = "",
        ) = MirrorJitpackDependency(MindustryPlugin.MindustryJitpackMirrorRepo, version)
    }

    inner class ArcSpec {
        infix fun on(version: String) {
            arcDependency.set(ArcDependency(version))
        }

        fun on(map: Map<String, Any>) {
            val version = map["version"]?.toString() ?: throw GradleException("No version specified for `arc.on`")
            arcDependency.set(ArcDependency(version))
        }
    }

    inner class MindustrySpec {
        infix fun mirror(version: String) {
            mindustryDependency.set(MirrorDependency(version))
        }

        infix fun on(version: String) {
            mindustryDependency.set(MindustryDependency(version))
        }

        fun mirror(map: Map<String, Any>) {
            val version = map["version"]?.toString() ?: throw GradleException("No version specified for `mindustry.mirror`")
            mindustryDependency.set(MirrorDependency(version))
        }

        fun on(map: Map<String, Any>) {
            val version = map["version"]?.toString() ?: throw GradleException("No version specified for `mindustry.on`")
            mindustryDependency.set(MindustryDependency(version))
        }
    }
}

interface IGameLocationSpec {
    fun GameLocation(
        user: String = "",
        repo: String = "",
        version: String = "",
        release: String = "",
    ) = io.github.liplum.mindustry.GameLocation(user, repo, version, release)

    infix fun official(version: String)
    infix fun be(version: String)
    infix fun official(map: Map<String, Any>)
    infix fun be(map: Map<String, Any>)
}

class ClientSpec(
    target: Project,
) : IGameLocationSpec {
    val location = target.prop<GameLocation>().apply {
        convention(
            GameLocation(
                user = MindustryPlugin.Anuken, repo = MindustryPlugin.Mindustry,
                version = MindustryPlugin.DefaultMindustryVersion,
                release = MindustryPlugin.ClientReleaseName
            )
        )
    }
    val mindustry: ClientSpec
        get() = this

    override infix fun official(
        version: String,
    ) {
        location.set(
            GameLocation(
                user = MindustryPlugin.Anuken, repo = MindustryPlugin.Mindustry,
                version = version,
                release = MindustryPlugin.ClientReleaseName
            )
        )
    }

    override infix fun be(
        version: String,
    ) {
        location.set(
            GameLocation(
                MindustryPlugin.Anuken, MindustryPlugin.MindustryBuilds,
                version, "Mindustry-BE-Desktop-$version.jar"
            )
        )
    }

    override infix fun official(
        map: Map<String, Any>,
    ) {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified in `official`")
        official(version)
    }

    override infix fun be(
        map: Map<String, Any>,
    ) {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified in `be`")
        be(version)
    }
}

class ServerSpec(
    target: Project,
) : IGameLocationSpec {
    val location = target.prop<GameLocation>().apply {
        convention(
            GameLocation(
                user = MindustryPlugin.Anuken, repo = MindustryPlugin.Mindustry,
                version = MindustryPlugin.DefaultArcVersion,
                release = MindustryPlugin.ServerReleaseName
            )
        )
    }
    val mindustry: ServerSpec
        get() = this

    override infix fun official(
        version: String,
    ) {
        location.set(
            GameLocation(
                user = MindustryPlugin.Anuken, repo = MindustryPlugin.Mindustry,
                version = version,
                release = MindustryPlugin.ServerReleaseName
            )
        )
    }

    override infix fun be(
        version: String,
    ) {
        location.set(
            GameLocation(
                MindustryPlugin.Anuken, MindustryPlugin.MindustryBuilds,
                version, "Mindustry-BE-Server-$version.jar"
            )
        )
    }

    override infix fun official(
        map: Map<String, Any>,
    ) {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified in `official`")
        official(version)
    }

    override infix fun be(
        map: Map<String, Any>,
    ) {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified in `be`")
        be(version)
    }
}

class ModsSpec(
    target: Project,
) {
    val extraModsFromTask = target.stringsProp().apply {
        convention(
            if (target.plugins.hasPlugin(JavaPlugin::class.java))
                listOf(JavaPlugin.JAR_TASK_NAME)
            else emptyList()
        )
    }
    val worksWith = target.listProp<IMod>().apply {
        convention(HashSet())
    }
    val add: ModsSpec
        get() = this

    fun worksWith(config: Runnable) {
        config.run()
    }

    inline fun worksWith(config: () -> Unit) {
        config()
    }

    infix fun github(repo: String) {
        worksWith.add(GitHubMod(repo))
    }

    infix fun local(path: String) {
        worksWith.add(LocalMod(path))
    }

    infix fun url(url: String) {
        worksWith.add(UrlMod(url))
    }
    /**
     * Add some mods working with this mod.
     */
    fun worksWith(vararg mods: IMod) {
        worksWith.addAll(mods.toList())
    }

    fun Mod(map: Map<String, Any>): IMod {
        run {
            val repo = map["repo"]
            if (repo != null) {
                return GitHubMod(repo.toString())
            }
        }
        run {
            val path = map["path"]
            if (path != null) {
                return LocalMod(path.toString())
            }
        }
        run {
            val url = map["url"]
            if (url != null) {
                return UrlMod(url.toString())
            }
        }
        throw RuntimeException("Unknown mod type from $map")
    }

    companion object {
        fun GitHub(repo: String) = GitHubMod(repo)
        fun Local(path: String) = LocalMod(path)
        fun Url(url: String) = UrlMod(url)
    }
}

class DeploySpec(
    target: Project,
) {
    val outputJarName = target.stringProp().apply {
        convention("")
    }
    val jarClassifier = target.stringProp().apply {
        convention("")
    }
    val androidSdkRoot = target.stringProp().apply {
        convention(System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT") ?: "")
    }
    // For Groovy
    fun propertyMissing(property: String): Any? =
        when (property) {
            "outputName" -> outputJarName.get()
            "classifier" -> jarClassifier.get()
            else -> null
        }
    // For Groovy
    fun propertyMissing(property: String, value: Any): Any? =
        when (property) {
            "outputName" -> outputJarName.set(value.toString())
            "classifier" -> jarClassifier.set(value.toString())
            else -> null
        }
}

class RunSpec(
    target: Project,
) {
    val dataDir = target.stringProp().apply {
        convention("temp")
    }
    var DataDir: String
        get() = dataDir.getOrElse("")
        set(value) {
            dataDir.set(value)
        }

    fun setDataDefault() {
        dataDir.set("")
    }

    fun setDataTemp() {
        dataDir.set("temp")
    }
}