@file:Suppress("RemoveRedundantBackticks")
@file:JvmName("Extension")
package plumy.mindustry

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import plumy.dsl.listProp
import plumy.dsl.prop
import plumy.dsl.stringProp
import plumy.dsl.stringsProp

open class MindustryExtension(
    target: Project,
) {
    /**
     * Import v135 as default for now.
     * DO NOT trust this behavior, it may change later.
     */
    val arcDependency = target.prop<Dependency>().apply {
        convention(Dependency(Meta.ArcJitpackRepo, Meta.DefaultMindustryVersion))
    }
    /**
     * Import v135 as default for now.
     * DO NOT trust this behavior, it may change later.
     */
    val mindustryDependency = target.prop<Dependency>().apply {
        convention(Dependency(Meta.MindustryJitpackRepo, Meta.DefaultMindustryVersion))
    }
    val clientLocation = target.prop<GameLocation>().apply {
        convention(
            GameLocation(
                user = "anuken", repo = "mindustry",
                version = Meta.DefaultMindustryVersion,
                releaseName = Meta.ClientReleaseName
            )
        )
    }
    val severLocation = target.prop<GameLocation>().apply {
        convention(
            GameLocation(
                user = "anuken", repo = "mindustry",
                version = Meta.DefaultArcVersion,
                releaseName = Meta.ServerReleaseName
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
    @JvmOverloads
    fun Dependency(
        fullName: String = "",
        version: String = "",
    ) = plumy.mindustry.Dependency(fullName, version)

    @JvmOverloads
    fun GameLocation(
        user: String = "",
        repo: String = "",
        version: String = "",
        releaseName: String = "",
    ) = plumy.mindustry.GameLocation(user, repo, version, releaseName)
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
        convention(emptyList())
    }
    val keepMods = target.stringsProp().apply {
        convention(emptyList())
    }
    val workWith = target.listProp<IMod>().apply {
        convention(emptyList())
    }
}

class Deploy(
    target: Project,
) {
    val androidSdkRoot = target.stringProp().apply {
        convention(System.getenv("ANDROID_HOME") ?: System.getenv("ANDROID_SDK_ROOT") ?: "")
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
    fun ModMeta(info: Map<String, Any>) =
        plumy.mindustry.ModMeta(info)
    /**
     * @see [plumy.mindustry.ModMeta]
     */
    @JvmOverloads
    fun ModMeta(
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
    ) = plumy.mindustry.ModMeta(
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