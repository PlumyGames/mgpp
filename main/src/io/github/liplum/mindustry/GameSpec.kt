package io.github.liplum.mindustry

import arc.util.serialization.Jval
import io.github.liplum.dsl.BoolProp
import io.github.liplum.dsl.StringsProp
import io.github.liplum.dsl.prop
import io.github.liplum.dsl.stringsProp
import io.github.liplum.mindustry.LocalProperties.localProperties
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
    abstract val location: Property<IGameLocation>
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
        get() = keepOtherVersion.set(true)
    /**
     * Keep other versions when a new version is downloaded.
     */
    val keepOthers: Unit
        get() = keepOtherVersion.set(false)
    /**
     * @see [GitHubGameLocation]
     */
    fun GameLocation(
        user: String = "",
        repo: String = "",
        version: String = "",
        release: String = "",
    ) = GitHubLocation(user, repo, version, release)
    /**
     * @see [GitHubGameLocation]
     */
    fun GameLocation(
        map: Map<String, String>,
    ) = GitHubLocation(map)
    /**
     * @see [GitHubGameLocation]
     */
    fun GitHubLocation(
        user: String = "",
        repo: String = "",
        version: String = "",
        release: String = "",
    ) = GitHubGameLocation(user, repo, version, release)
    /**
     * @see [GitHubGameLocation]
     */
    fun GitHubLocation(
        map: Map<String, String>,
    ) = GitHubGameLocation(
        user = map["user"] ?: "",
        repo = map["repo"] ?: "",
        version = map["version"] ?: "",
        release = map["release"] ?: "",
    )
    /**
     * @see [LocalGameLocation]
     */
    fun LocalLocation(file: File) = LocalGameLocation(file)
    /**
     * @see [LocalGameLocation]
     */
    fun LocalLocation(path: String) = LocalGameLocation(File(path))
    /**
     * Get a local game location from the value of "mgpp.$type.location" in `local.properties`
     *
     * type: `client` or `server`
     */
    fun LocalPropertyLocation(): IGameLocation {
        val key = "mgpp.$type.location"
        val value = target.localProperties.getProperty(key)
        return if (value != null) LocalLocation(value)
        else {
            target.logger.warn("$key isn't in the local.properties.")
            Official(Mgpp.DefaultMindustryVersion)
        }
    }
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
     * A notation represents the `local.properties`
     * ## Usages
     * ```kotlin
     * client {
     *      mindustry from localProperties
     * }
     * ```
     */
    val localProperties: LocalPropertiesNotation
        get() = LocalPropertiesNotation
    /**
     * Set the [location] to [game]
     */
    infix fun <T> from(game: T): T where T : IGameLocation =
        game.apply {
            location.set(this)
        }
    /**
     * Download official edition from [MindustryPlugin.MindustryOfficialReleaseURL]
     */
    infix fun official(version: String): GitHubGameLocation =
        Official(version).apply {
            location.set(this)
        }
    /**
     * Download bleeding-edge from [MindustryPlugin.APIMindustryBEReleaseURL]
     */
    infix fun be(version: String): GitHubGameLocation =
        BE(version).apply {
            location.set(this)
        }
    /**
     * Download official edition from [MindustryPlugin.MindustryOfficialReleaseURL]
     * ## Supported notations:
     * - [latest]: set the [location] to the latest official
     */
    infix fun official(notation: INotation): GitHubGameLocation =
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
    infix fun be(latest: INotation): GitHubGameLocation =
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
     * **Also** You could try [fromLocalProperties] to set a different game location for everyone.
     */
    infix fun fromLocal(file: File): LocalGameLocation =
        LocalLocation(file).apply {
            location.set(this)
        }
    /**
     * Copy the game from local file at [path]
     *
     * **Suggestion** To use a relative path would be better for git or collaboration.
     * Don't embed the whole game into project directory.
     */
    infix fun fromLocal(path: String): LocalGameLocation =
        LocalLocation(path).apply {
            location.set(this)
        }
    /**
     * Set [location] to a local game by the value of "mgpp.$type.location" in `local.properties`
     *
     * type: `client` or `server`
     * @see [LocalPropertyLocation]
     */
    fun fromLocalProperties(): IGameLocation =
        LocalPropertyLocation().apply {
            location.set(this)
        }
    /**
     * ## Supported notations:
     * - [localProperties]: see [fromLocalProperties]
     */
    infix fun from(notation: INotation): IGameLocation =
        when (notation) {
            LocalPropertiesNotation -> fromLocalProperties()
            else -> throw GradleException("Unknown $type notation of mindustry $notation")
        }
    /**
     * Create a [GitHubGameLocation] of official edition from [MindustryPlugin.APIMindustryOfficialReleaseURL]
     */
    abstract fun Official(version: String): GitHubGameLocation
    /**
     * Create a [GitHubGameLocation] of bleeding-edge from [MindustryPlugin.APIMindustryBEReleaseURL]
     */
    abstract fun BE(version: String): GitHubGameLocation
    /**
     * Create a [GitHubGameLocation] of the latest official edition from [MindustryPlugin.APIMindustryOfficialReleaseURL]
     *
     * **Not Recommended** It may not work due to a network issue or GitHub API access limitation.
     */
    fun LatestOfficial(): GitHubGameLocation {
        val latestVersion = target.getLatestVersion("mindustry-$type-official") {
            try {
                val url = URL(Mgpp.APIMindustryOfficialLatestReleaseURL)
                val json = Jval.read(url.readText())
                val version = json.getString("tag_name").let {
                    if (it == null) {
                        target.logger.warn("Can't fetch latest official.")
                        Mgpp.DefaultMindustryVersion
                    } else it
                }
                return@getLatestVersion version
            } catch (e: Exception) {
                target.logger.warn(
                    "Can't fetch latest official version, so use ${Mgpp.DefaultMindustryVersion} as default instead.",
                    e
                )
                return@getLatestVersion Mgpp.DefaultMindustryVersion
            }
        }
        return Official(latestVersion)
    }
    /**
     * Create a [GitHubGameLocation] of the latest bleeding-edge from [MindustryPlugin.APIMindustryBEReleaseURL]
     *
     * **Not Recommended** It may not work due to a network issue or GitHub API access limitation.
     */
    fun LatestBE(): GitHubGameLocation {
        val latestVersion = target.getLatestVersion("mindustry-$type-be") {
            try {
                val url = URL(Mgpp.APIMindustryBELatestReleaseURL)
                val json = Jval.read(url.readText())
                val version = json.getString("tag_name").let {
                    if (it == null) {
                        target.logger.warn("Can't fetch latest be.")
                        Mgpp.DefaultMindustryBEVersion
                    } else it
                }
                return@getLatestVersion version
            } catch (e: Exception) {
                target.logger.warn(
                    "Can't fetch latest be version, so use ${Mgpp.DefaultMindustryBEVersion} as default instead.",
                    e
                )
                return@getLatestVersion Mgpp.DefaultMindustryBEVersion
            }
        }
        return BE(latestVersion)
    }
    /**
     * Download official edition from [MindustryPlugin.APIMindustryOfficialLatestReleaseURL]
     * ## Supported notations:
     * - [latest]: set the [location] to the latest official
     */
    infix fun official(map: Map<String, Any>): GitHubGameLocation {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified in `official`")
        return when (version) {
            LatestNotation.toString() -> official(LatestNotation)
            else -> official(version)
        }
    }
    /**
     * Create a [GitHubGameLocation] of bleeding-edge from [MindustryPlugin.APIMindustryOfficialLatestReleaseURL]
     * ## Supported notations:
     * - [latest]: set the [location] to the latest bleeding-edge
     */
    infix fun be(map: Map<String, Any>): GitHubGameLocation {
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
    @InheritFromParent
    @LocalProperty("mgpp.client.location")
    override val location = target.prop<IGameLocation>().apply {
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
    @InheritFromParent
    @LocalProperty("mgpp.server.location")
    override val location = target.prop<IGameLocation>().apply {
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