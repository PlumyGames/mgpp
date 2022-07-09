package io.github.liplum.mindustry

import arc.util.serialization.Jval
import io.github.liplum.dsl.prop
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.io.File
import java.net.URL

interface IGameSpec {
    /**
     * The location of a game on GitHub
     */
    @InheritFromParent
    val location: Property<IGameLocation>
    /**
     * Whether to keep other versions when a new version is downloaded.
     */
    val keepOtherVersion: Property<Boolean>
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
    val target: Project
    /**
     * @see [GitHubGameLocation]
     */
    fun GameLocation(
        user: String = "",
        repo: String = "",
        version: String = "",
        release: String = "",
    ) = GitHubGameLocation(user, repo, version, release)
    /**
     * @see [LocalGameLocation]
     */
    fun LocalLocation(file: File) = LocalGameLocation(file)
    /**
     * @see [LocalGameLocation]
     */
    fun LocalLocation(path: String) = LocalGameLocation(File(path))
    /**
     * A notation represents the latest version.
     * ## Usages
     * ```
     * client {
     *     mindustry be latest
     * }
     *
     * This is not recommended, it might work if you faced the API limit of GitHub.
     * ```
     */
    val latest: LatestNotation
        get() = LatestNotation
    /**
     * Download official edition from [MindustryPlugin.OfficialReleaseURL]
     */
    infix fun official(version: String): GitHubGameLocation =
        Official(version).apply {
            location.set(this)
        }
    /**
     * Download bleeding-edge from [MindustryPlugin.BEReleaseURL]
     */
    infix fun be(version: String): GitHubGameLocation =
        BE(version).apply {
            location.set(this)
        }
    /**
     * Download official edition from [MindustryPlugin.OfficialReleaseURL]
     */
    infix fun official(notation: INotation): GitHubGameLocation =
        LatestOfficial().apply {
            if (notation === LatestNotation)
                location.set(this)
            else
                throw GradleException("Unknown game notation of official $notation")
        }
    /**
     * Download bleeding-edge from [MindustryPlugin.OfficialReleaseURL]
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
     */
    infix fun fromLocal(file: File): LocalGameLocation =
        LocalLocation(file).apply {
            location.set(this)
        }
    /**
     * Copy the game from local file at [path]
     */
    infix fun fromLocal(path: String): LocalGameLocation =
        LocalLocation(path).apply {
            location.set(this)
        }
    /**
     * Create a [GitHubGameLocation] of official edition from [MindustryPlugin.OfficialReleaseURL]
     */
    fun Official(version: String): GitHubGameLocation
    /**
     * Create a [GitHubGameLocation] of bleeding-edge from [MindustryPlugin.OfficialReleaseURL]
     */
    fun BE(version: String): GitHubGameLocation
    /**
     * Create a [GitHubGameLocation] of the latest official edition from [MindustryPlugin.OfficialReleaseURL]
     */
    fun LatestOfficial(): GitHubGameLocation {
        return try {
            val url = URL(Mgpp.OfficialReleaseURL)
            val json = Jval.read(url.readText())
            val version = json.getString("tag_name").let {
                if (it == null) {
                    target.logger.warn("Can't fetch latest official.")
                    Mgpp.DefaultMindustryVersion
                } else it
            }
            Official(version)
        } catch (e: Exception) {
            target.logger.warn(
                "Can't fetch latest official version, so use ${Mgpp.DefaultMindustryVersion} as default instead.",
                e
            )
            Official(Mgpp.DefaultMindustryVersion)
        }
    }
    /**
     * Create a [GitHubGameLocation] of the latest bleeding-edge from [MindustryPlugin.OfficialReleaseURL]
     */
    fun LatestBE(): GitHubGameLocation {
        try {
            val url = URL(Mgpp.BEReleaseURL)
            val json = Jval.read(url.readText())
            val version = json.getString("tag_name").let {
                if (it == null) {
                    target.logger.warn("Can't fetch latest be.")
                    Mgpp.DefaultMindustryBEVersion
                } else it
            }
            return BE(version)
        } catch (e: Exception) {
            target.logger.warn(
                "Can't fetch latest be version, so use ${Mgpp.DefaultMindustryBEVersion} as default instead.",
                e
            )
            return BE(Mgpp.DefaultMindustryBEVersion)
        }
    }
    /**
     * Download official edition from [MindustryPlugin.OfficialReleaseURL]
     */
    infix fun official(map: Map<String, Any>): GitHubGameLocation {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified in `official`")
        return if (version == "latest") {
            official(LatestNotation)
        } else {
            official(version)
        }
    }
    /**
     * Create a [GitHubGameLocation] of bleeding-edge from [MindustryPlugin.OfficialReleaseURL]
     */
    infix fun be(map: Map<String, Any>): GitHubGameLocation {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified in `be`")
        return if (version == "latest") {
            be(LatestNotation)
        } else {
            be(version)
        }
    }
}
/**
 * You can set up the client which you want to run and debug your mod on.
 * @see IGameSpec
 */
class ClientSpec(
    override val target: Project,
) : IGameSpec {
    override val keepOtherVersion = target.prop<Boolean>().apply {
        convention(false)
    }
    @InheritFromParent
    override val location = target.prop<IGameLocation>().apply {
        convention(Official(version = Mgpp.DefaultMindustryVersion))
    }
    val mindustry: ClientSpec
        get() = this

    override infix fun Official(
        version: String,
    ) = GameLocation(
        user = Mgpp.Anuken, repo = Mgpp.Mindustry,
        version = version,
        release = Mgpp.ClientReleaseName
    )

    override infix fun BE(
        version: String,
    ) = GameLocation(
        Mgpp.Anuken, Mgpp.MindustryBuilds,
        version, "Mindustry-BE-Desktop-$version.jar"
    )
}
/**
 * You can set up the server which you want to run and debug your mod on.
 * @see IGameSpec
 */
class ServerSpec(
    override val target: Project,
) : IGameSpec {
    override val keepOtherVersion = target.prop<Boolean>().apply {
        convention(false)
    }
    @InheritFromParent
    override val location = target.prop<IGameLocation>().apply {
        convention(Official(version = Mgpp.DefaultMindustryVersion))
    }
    val mindustry: ServerSpec
        get() = this

    override fun Official(
        version: String,
    ) = GameLocation(
        user = Mgpp.Anuken, repo = Mgpp.Mindustry,
        version = version,
        release = Mgpp.ServerReleaseName
    )

    override fun BE(
        version: String,
    ) = GameLocation(
        Mgpp.Anuken, Mgpp.MindustryBuilds,
        version, "Mindustry-BE-Server-$version.jar"
    )
}