@file:Suppress("RemoveRedundantBackticks")
@file:JvmName("Extension")

package plumy.mindustry

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPlugin
import plumy.dsl.*

open class MindustryExtension(
    target: Project,
) {
    @JvmField
    val Mod = ProjectType.Mod
    @JvmField
    val Plugin = ProjectType.Plugin
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

    val assets = AssetSpec(target)
    fun assets(func: Action<AssetSpec>) {
        func.execute(assets)
    }

    inline fun assets(func: AssetSpec.() -> Unit) {
        assets.func()
    }

    val deploy = DeploySpec(target)
    fun deploy(func: Action<DeploySpec>) {
        func.execute(deploy)
    }

    inline fun deploy(func: DeploySpec.() -> Unit) {
        deploy.func()
    }
}
/**
 * Retrieves the [mindustry][MindustryExtension] extension.
 */
val Project.`mindustry`: MindustryExtension
    get() = (this as ExtensionAware).extensions.getByName(Meta.ExtensionName) as MindustryExtension
/**
 * Configures the [mindustry][MindustryExtension] extension.
 */
fun Project.`mindustry`(configure: Action<MindustryExtension>): Unit =
    (this as ExtensionAware).extensions.configure(Meta.ExtensionName, configure)

class DependencySpec(
    target: Project,
) {
    @JvmField
    val ArcRepo = Meta.ArcJitpackRepo
    @JvmField
    val MindustryMirrorRepo = Meta.MindustryJitpackMirrorRepo
    @JvmField
    val MindustryRepo = Meta.MindustryJitpackRepo
    /**
     * Import v135 as default for now.
     * DO NOT trust this behavior, it may change later.
     */
    val arc = target.prop<IDependency>().apply {
        convention(ArcDependency())
    }
    /**
     * Import v135 as default for now.
     * DO NOT trust this behavior, it may change later.
     */
    val mindustry = target.prop<IDependency>().apply {
        convention(MindustryDependency())
    }

    fun MirrorDependency(
        version: String = "",
    ) = MirrorJitpackDependency(Meta.MindustryJitpackMirrorRepo, version)

    fun useMirror(
        version: String = "",
    ) {
        mindustry.set(MirrorDependency(version))
    }

    fun mindustry(
        version: String = "",
    ) {
        mindustry.set(MindustryDependency(version))
    }

    fun arc(
        version: String = "",
    ) {
        arc.set(ArcDependency(version))
    }

    fun ArcDependency(
        version: String = Meta.DefaultMindustryVersion,
    ) = Dependency(Meta.ArcJitpackRepo, version)

    fun MindustryDependency(
        version: String = Meta.DefaultMindustryVersion,
    ) = Dependency(Meta.MindustryJitpackRepo, version)

    fun Dependency(
        fullName: String = "",
        version: String = "",
    ) = plumy.mindustry.Dependency(fullName, version)
}

interface IGameLocationSpec {
    fun GameLocation(
        user: String = "",
        repo: String = "",
        version: String = "",
        release: String = "",
    ) = plumy.mindustry.GameLocation(user, repo, version, release)
}

class ClientSpec(
    target: Project,
) : IGameLocationSpec {
    val location = target.prop<GameLocation>().apply {
        convention(
            GameLocation(
                user = Meta.Anuken, repo = Meta.Mindustry,
                version = Meta.DefaultMindustryVersion,
                release = Meta.ClientReleaseName
            )
        )
    }

    fun official(
        version: String = "",
    ) {
        location.set(
            GameLocation(
                user = Meta.Anuken, repo = Meta.Mindustry,
                version = version,
                release = Meta.ClientReleaseName
            )
        )
    }

    fun be(
        version: String = "",
    ) {
        location.set(
            GameLocation(
                Meta.Anuken, Meta.MindustryBuilds,
                version, "Mindustry-BE-Desktop-$version.jar"
            )
        )
    }
}

class ServerSpec(
    target: Project,
) : IGameLocationSpec {
    val location = target.prop<GameLocation>().apply {
        convention(
            GameLocation(
                user = Meta.Anuken, repo = Meta.Mindustry,
                version = Meta.DefaultArcVersion,
                release = Meta.ServerReleaseName
            )
        )
    }

    fun official(
        version: String = "",
    ) {
        location.set(
            GameLocation(
                user = Meta.Anuken, repo = Meta.Mindustry,
                version = version,
                release = Meta.ServerReleaseName
            )
        )
    }

    fun be(
        version: String = "",
    ) {
        location.set(
            GameLocation(
                Meta.Anuken, Meta.MindustryBuilds,
                version, "Mindustry-BE-Server-$version.jar"
            )
        )
    }
}

class ModsSpec(
    target: Project,
) {
    val extraModsFromTask = target.stringsProp().apply {
        convention(listOf(JavaPlugin.JAR_TASK_NAME))
    }
    val worksWith = target.listProp<IMod>().apply {
        convention(emptyList())
    }
    /**
     * Add some mods working with this mod.
     */
    fun worksWith(vararg mods: IMod) {
        val old = worksWith.getOrElse(emptyList())
        worksWith.set(old + mods.toList())
    }
    /**
     * Add some mods working with this mod.
     */
    fun worksWith(mods: Map<String, Any>) {
        val addition = ArrayList<IMod>(mods.size)
        for ((typeRaw, modRaw) in mods) {
            val mod = modRaw.toString()
            val type = typeRaw.lowercase()
            if (type.startsWith("local")) {
                addition.add(LocalMod(mod))
            } else if (type.startsWith("repo")) {
                addition.add(GitHubMod(mod))
            } else if (type.startsWith("url")) {
                addition.add(UrlMod(mod))
            }
        }
        val old = worksWith.getOrElse(emptyList())
        worksWith.set(old + addition)
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
        func {
            val url = map["url"]
            if (url != null) {
                return UrlMod(url.toString())
            }
        }
        throw RuntimeException("Unknown mod type from $map")
    }

    fun GitHub(repo: String) = GitHubMod(repo)
    fun Local(path: String) = LocalMod(path)
    fun URL(url: String) = UrlMod(url)
}

class DeploySpec(
    target: Project,
) {
    val androidSdkRoot = target.stringProp().apply {
        convention(System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT") ?: "")
    }
}

class RunSpec(
    target: Project,
) {
    val dataDir = target.stringProp().apply {
        convention("")
    }
    var DataDir: String
        get() = dataDir.getOrElse("")
        set(value) {
            dataDir.set(value)
        }

    fun setDataTemp() {
        dataDir.set("temp")
    }
}

class AssetSpec(
    target: Project,
) {
    val modMeta = target.prop<ModMeta>().apply {
        convention(ModMeta.fromHjson(
            target.rootDir.resolve("mod.hjson").let {
                if (it.exists()) it else target.rootDir.resolve("mod.json")
            }
        ))
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
     * @see [plumy.mindustry.ModMeta]
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
     * @see [plumy.mindustry.ModMeta]
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
        modMeta.set(this)
    }
}