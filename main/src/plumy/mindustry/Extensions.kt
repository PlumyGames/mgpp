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
        convention(Dependency(Meta.ArcJitpackRepo, Meta.DefaultMindustryVersion))
    }
    /**
     * Import v135 as default for now.
     * DO NOT trust this behavior, it may change later.
     */
    val mindustry = target.prop<IDependency>().apply {
        convention(Dependency(Meta.MindustryJitpackRepo, Meta.DefaultMindustryVersion))
    }
    val client = target.prop<GameLocation>().apply {
        convention(
            GameLocation(
                user = "anuken", repo = "mindustry",
                version = Meta.DefaultMindustryVersion,
                release = Meta.ClientReleaseName
            )
        )
    }
    val sever = target.prop<GameLocation>().apply {
        convention(
            GameLocation(
                user = "anuken", repo = "mindustry",
                version = Meta.DefaultArcVersion,
                release = Meta.ServerReleaseName
            )
        )
    }
    val projectType = target.prop<ProjectType>().apply {
        convention(ProjectType.Mod)
    }
    val mods = Mods(target)
    fun mods(func: Action<Mods>) {
        func.execute(mods)
    }

    inline fun mods(func: Mods.() -> Unit) {
        mods.func()
    }
    val run = Run(target)
    fun run(func: Action<Run>) {
        func.execute(run)
    }

    inline fun run(func: Run.() -> Unit) {
        run.func()
    }
    val assets = Asset(target)
    fun assets(func: Action<Asset>) {
        func.execute(assets)
    }

    inline fun assets(func: Asset.() -> Unit) {
        assets.func()
    }
    val deploy = Deploy(target)
    fun deploy(func: Action<Deploy>) {
        func.execute(deploy)
    }

    inline fun deploy(func: Deploy.() -> Unit) {
        deploy.func()
    }

    fun MirrorDependency(
        fullName: String = Meta.MindustryJitpackMirrorRepo,
        version: String = "",
    ) = MirrorJitpackDependency(fullName, version)

    fun Dependency(
        fullName: String = "",
        version: String = "",
    ) = plumy.mindustry.Dependency(fullName, version)

    fun GameLocation(
        user: String = "",
        repo: String = "",
        version: String = "",
        release: String = "",
    ) = plumy.mindustry.GameLocation(user, repo, version, release)
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

class Mods(
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

class Deploy(
    target: Project,
) {
    val androidSdkRoot = target.stringProp().apply {
        convention(System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT") ?: "")
    }
}

class Run(
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

class Asset(
    target: Project,
) {
    val modMeta = target.prop<ModMeta>().apply {
        convention(ModMeta.fromHjson(
            target.rootDir.resolve("mod.hjson").let {
                if (it.exists()) it else target.rootDir.resolve("mod.json")
            }
        ))
    }
    /**
     * @see [plumy.mindustry.ModMeta]
     */
    fun modMeta(info: Map<String, Any>) =
        ModMeta(info).apply {
            modMeta.set(this)
        }
    /**
     * @see [plumy.mindustry.ModMeta]
     */
    @JvmOverloads
    fun modMeta(
        name: String = "",
        displayName: String = "",
        author: String = "",
        description: String = "",
        subtitle: String = "",
        version: String = "1.0",
        main: String = "",
        minGameVersion: String = Meta.DefaultMinGameVersion,
        repo: String = "",
        dependencies: List<String> = emptyList(),
        hidden: Boolean = false,
        java: Boolean = true,
        hideBrowser: Boolean = true,
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