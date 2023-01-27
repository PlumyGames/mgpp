package io.github.liplum.mindustry

import arc.util.serialization.Jval
import io.github.liplum.dsl.BoolProp
import io.github.liplum.dsl.StringsProp
import io.github.liplum.dsl.prop
import io.github.liplum.dsl.stringsProp
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.io.File
import java.net.URL

abstract class GameSpecBase(
    val target: Project,
    val type: String,
) {
    /**
     * The location of Mindustry game.
     */
    @InheritFromParent
    abstract val location: Property<IGameLoc>
    /**
     * Whether to keep other versions when a new version is downloaded.
     */
    @InheritFromParent
    abstract val keepOtherVersion: BoolProp
    /**
     * The extra startup arguments for Mindustry game.
     */
    @InheritFromParent
    abstract val startupArgs: StringsProp
    var args: List<String>
        get() = startupArgs.get()
        set(value) {
            startupArgs.set(value)
        }
    /**
     * Clean all other versions when a new version is downloaded.
     */
    val clearUp: Unit
        get() = keepOtherVersion.set(false)
    /**
     * Keep other versions when a new version is downloaded.
     */
    val keepOthers: Unit
        get() = keepOtherVersion.set(true)
    /**
     * @see [GitHubGameLoc]
     */
    fun GameLocation(
        user: String = "",
        repo: String = "",
        version: String = "",
        release: String = "",
    ) = GitHubLocation(user, repo, version, release)
    /**
     * @see [GitHubGameLoc]
     */
    fun GameLocation(
        map: Map<String, String>,
    ) = GitHubLocation(map)
    /**
     * @see [GitHubGameLoc]
     */
    fun GitHubLocation(
        user: String = "",
        repo: String = "",
        version: String = "",
        release: String = "",
    ) = GitHubGameLoc(user, repo, version, release)
    /**
     * @see [GitHubGameLoc]
     */
    fun GitHubLocation(
        map: Map<String, String>,
    ) = GitHubGameLoc(
        user = map["user"] ?: "",
        repo = map["repo"] ?: "",
        tag = map["version"] ?: "",
        file = map["release"] ?: "",
    )
    /**
     * @see [LocalGameLoc]
     */
    fun LocalLocation(file: File) = LocalGameLoc(file)
    /**
     * @see [LocalGameLoc]
     */
    fun LocalLocation(path: String) = LocalGameLoc(File(path))
    /**
     * A notation represents the latest version.
     * ## Usages
     * ```kotlin
     * client {
     *     mindustry be latest
     *     mindustry official latest
     * }
     * ```
     * **Not Recommended** It might not work if you faced the API limit of GitHub.
     */
    val latest: LatestNotation
        get() = LatestNotation
    /**
     * Set the [location] to [game]
     */
    infix fun <T> from(game: T): T where T : IGameLoc =
        game.apply {
            location.set(this)
        }
    /**
     * Download official edition from [MindustryPlugin.MindustryOfficialReleaseURL]
     */
    infix fun official(version: String): GitHubGameLoc =
        Official(version).apply {
            location.set(this)
        }
    /**
     * Download bleeding-edge from [MindustryPlugin.APIMindustryBEReleaseURL]
     */
    infix fun be(version: String): GitHubGameLoc =
        BE(version).apply {
            location.set(this)
        }
    /**
     * Download official edition from [MindustryPlugin.MindustryOfficialReleaseURL]
     * ## Supported notations:
     * - [latest]: set the [location] to the latest official
     */
    infix fun official(notation: INotation): GitHubGameLoc =
        LatestOfficial().apply {
            if (notation === LatestNotation)
                location.set(this)
            else
                throw GradleException("Unknown game notation of official $notation")
        }
    /**
     * Download bleeding-edge from [MindustryPlugin.APIMindustryBEReleaseURL]
     * ## Supported notations:
     * - [latest]: set the [location] to the latest bleeding-edge
     */
    infix fun be(latest: INotation): GitHubGameLoc =
        LatestBE().apply {
            if (latest === LatestNotation)
                location.set(this)
            else
                throw GradleException("Unknown game notation of be $latest")
        }
    /**
     * Copy the game from local [file]
     *
     * **Suggestion** To use a relative path would be better for git or collaboration.
     * Don't embed the whole game into project directory.
     *
     */
    infix fun fromLocal(file: File): LocalGameLoc =
        LocalLocation(file).apply {
            location.set(this)
        }
    /**
     * Copy the game from local file at [path]
     *
     * **Suggestion** To use a relative path would be better for git or collaboration.
     * Don't embed the whole game into project directory.
     */
    infix fun fromLocal(path: String): LocalGameLoc =
        LocalLocation(path).apply {
            location.set(this)
        }
    /**
     * ## Supported notations:
     * - None
     */
    infix fun from(notation: INotation): IGameLoc =
        throw GradleException("Unknown $type notation of mindustry $notation")
    /**
     * Create a [GitHubGameLoc] of official edition from [MindustryPlugin.APIMindustryOfficialReleaseURL]
     */
    abstract fun Official(version: String): GitHubGameLoc
    /**
     * Create a [GitHubGameLoc] of bleeding-edge from [MindustryPlugin.APIMindustryBEReleaseURL]
     */
    abstract fun BE(version: String): GitHubGameLoc
    /**
     * Create a [GitHubGameLoc] of the latest official edition from [MindustryPlugin.APIMindustryOfficialReleaseURL]
     *
     * **Not Recommended** It may not work due to a network issue or GitHub API access limitation.
     */
    fun LatestOfficial(): GitHubGameLoc {
        val latestVersion = target.fetchLatestVersion("mindustry-$type-official") {
            try {
                val url = URL(Mgpp.APIMindustryOfficialLatestReleaseURL)
                val json = Jval.read(url.readText())
                val version = json.getString("tag_name").let {
                    if (it == null) {
                        target.logger.warn("Can't fetch latest official.")
                        Mgpp.DefaultMindustryVersion
                    } else it
                }
                return@fetchLatestVersion version
            } catch (e: Exception) {
                target.logger.warn(
                    "Can't fetch latest official version, so use ${Mgpp.DefaultMindustryVersion} as default instead.",
                    e
                )
                return@fetchLatestVersion Mgpp.DefaultMindustryVersion
            }
        }
        return Official(latestVersion)
    }
    /**
     * Create a [GitHubGameLoc] of the latest bleeding-edge from [MindustryPlugin.APIMindustryBEReleaseURL]
     *
     * **Not Recommended** It may not work due to a network issue or GitHub API access limitation.
     */
    fun LatestBE(): GitHubGameLoc {
        val latestVersion = target.fetchLatestVersion("mindustry-$type-be") {
            try {
                val url = URL(Mgpp.APIMindustryBELatestReleaseURL)
                val json = Jval.read(url.readText())
                val version = json.getString("tag_name").let {
                    if (it == null) {
                        target.logger.warn("Can't fetch latest be.")
                        Mgpp.DefaultMindustryBEVersion
                    } else it
                }
                return@fetchLatestVersion version
            } catch (e: Exception) {
                target.logger.warn(
                    "Can't fetch latest be version, so use ${Mgpp.DefaultMindustryBEVersion} as default instead.",
                    e
                )
                return@fetchLatestVersion Mgpp.DefaultMindustryBEVersion
            }
        }
        return BE(latestVersion)
    }
    /**
     * Download official edition from [MindustryPlugin.APIMindustryOfficialLatestReleaseURL]
     * ## Supported notations:
     * - [latest]: set the [location] to the latest official
     */
    infix fun official(map: Map<String, Any>): GitHubGameLoc {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified in `official`")
        return when (version) {
            LatestNotation.toString() -> official(LatestNotation)
            else -> official(version)
        }
    }
    /**
     * Create a [GitHubGameLoc] of bleeding-edge from [MindustryPlugin.APIMindustryOfficialLatestReleaseURL]
     * ## Supported notations:
     * - [latest]: set the [location] to the latest bleeding-edge
     */
    infix fun be(map: Map<String, Any>): GitHubGameLoc {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified in `be`")
        return when (version) {
            LatestNotation.toString() -> be(LatestNotation)
            else -> be(version)
        }
    }
}
/**
 * You can set up the client which you want to run and debug your mod on.
 * @see GameSpecBase
 */
class ClientSpec(
    target: Project,
) : GameSpecBase(target, "client") {
    @InheritFromParent
    override val keepOtherVersion = target.prop<Boolean>().apply {
        convention(false)
    }
    @InheritFromParent
    override val startupArgs = target.stringsProp()
    /**
     * The game location of client.
     *
     * `mgpp.client.location` in `local.properties` will overwrite this.
     */
    @InheritFromParent
    @LocalProperty("mgpp.client.location")
    override val location = target.prop<IGameLoc>().apply {
        convention(Official(version = Mgpp.DefaultMindustryVersion))
    }
    val mindustry: ClientSpec
        get() = this

    override fun Official(
        version: String,
    ) = GitHubLocation(
        user = Mgpp.Anuken, repo = Mgpp.Mindustry,
        version = version,
        release = Mgpp.ClientReleaseName
    )

    override fun BE(
        version: String,
    ) = GitHubLocation(
        Mgpp.Anuken, Mgpp.MindustryBuilds,
        version, "Mindustry-BE-Desktop-$version.jar"
    )

    fun Foo(
        version: String,
        release: String,
    ) = GitHubLocation(
        Mgpp.AntiGrief, Mgpp.FooClient,
        version, release
    )

    fun Foo(
        map: Map<String, String>,
    ) = Foo(
        version = map["version"] ?: "",
        release = map["release"] ?: "",
    )
}
/**
 * You can set up the server which you want to run and debug your mod on.
 * @see GameSpecBase
 */
class ServerSpec(
    target: Project,
) : GameSpecBase(target, "server") {
    @InheritFromParent
    override val keepOtherVersion = target.prop<Boolean>().apply {
        convention(false)
    }
    @InheritFromParent
    override val startupArgs = target.stringsProp()
    /**
     * The game location of client.
     *
     * `mgpp.server.location` in `local.properties` will overwrite this.
     */
    @InheritFromParent
    @LocalProperty("mgpp.server.location")
    override val location = target.prop<IGameLoc>().apply {
        convention(Official(version = Mgpp.DefaultMindustryVersion))
    }
    val mindustry: ServerSpec
        get() = this

    override fun Official(
        version: String,
    ) = GitHubLocation(
        user = Mgpp.Anuken, repo = Mgpp.Mindustry,
        version = version,
        release = Mgpp.ServerReleaseName
    )

    override fun BE(
        version: String,
    ) = GitHubLocation(
        Mgpp.Anuken, Mgpp.MindustryBuilds,
        version, "Mindustry-BE-Server-$version.jar"
    )
}