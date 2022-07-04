package io.github.liplum.mindustry

import arc.util.serialization.Jval
import io.github.liplum.dsl.prop
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.net.URL

interface IGameSpec {
    val location: Property<GameLocation>
    val keepOtherVersion: Property<Boolean>
    val clearUp: Unit
        get() = keepOtherVersion.set(true)
    val keepOthers: Unit
        get() = keepOtherVersion.set(false)
    val target: Project
    fun GameLocation(
        user: String = "",
        repo: String = "",
        version: String = "",
        release: String = "",
    ) = io.github.liplum.mindustry.GameLocation(user, repo, version, release)

    val latest: LatestNotation
        get() = LatestNotation

    infix fun official(version: String) {
        location.set(Official(version))
    }

    infix fun be(version: String) {
        location.set(BE(version))
    }

    infix fun official(notation: IMgppNotation) {
        if (notation === LatestNotation)
            this.location.set(LatestOfficial())
        else
            throw GradleException("Unknown game notation of official $notation")
    }

    infix fun be(latest: IMgppNotation) {
        if (latest === LatestNotation)
            this.location.set(LatestBE())
        else
            throw GradleException("Unknown game notation of be $latest")
    }

    fun Official(version: String): GameLocation
    fun BE(version: String): GameLocation
    fun LatestOfficial(): GameLocation {
        return try {
            val url = URL(MindustryPlugin.OfficialReleaseURL)
            val json = Jval.read(url.readText())
            val version = json.getString("tag_name").let {
                if (it == null) {
                    target.logger.warn("Can't fetch latest official.")
                    MindustryPlugin.DefaultMindustryVersion
                } else it
            }
            Official(version)
        } catch (e: Exception) {
            target.logger.warn(
                "Can't fetch latest official version, so use ${MindustryPlugin.DefaultMindustryVersion} as default instead.",
                e
            )
            Official(MindustryPlugin.DefaultMindustryVersion)
        }
    }

    fun LatestBE(): GameLocation {
        try {
            val url = URL(MindustryPlugin.BEReleaseURL)
            val json = Jval.read(url.readText())
            val version = json.getString("tag_name").let {
                if (it == null) {
                    target.logger.warn("Can't fetch latest be.")
                    MindustryPlugin.DefaultMindustryBEVersion
                } else it
            }
            return BE(version)
        } catch (e: Exception) {
            target.logger.warn(
                "Can't fetch latest be version, so use ${MindustryPlugin.DefaultMindustryBEVersion} as default instead.",
                e
            )
            return BE(MindustryPlugin.DefaultMindustryBEVersion)
        }
    }

    infix fun official(map: Map<String, Any>) {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified in `official`")
        if (version == "latest") {
            official(LatestNotation)
        } else {
            official(version)
        }
    }

    infix fun be(map: Map<String, Any>) {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified in `be`")
        if (version == "latest") {
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
    override val location = target.prop<GameLocation>().apply {
        convention(Official(version = MindustryPlugin.DefaultMindustryVersion))
    }
    val mindustry: ClientSpec
        get() = this

    override infix fun Official(
        version: String,
    ) = GameLocation(
        user = MindustryPlugin.Anuken, repo = MindustryPlugin.Mindustry,
        version = version,
        release = MindustryPlugin.ClientReleaseName
    )

    override infix fun BE(
        version: String,
    ) = GameLocation(
        MindustryPlugin.Anuken, MindustryPlugin.MindustryBuilds,
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
    override val location = target.prop<GameLocation>().apply {
        convention(Official(version = MindustryPlugin.DefaultMindustryVersion))
    }
    val mindustry: ServerSpec
        get() = this

    override fun Official(
        version: String,
    ) = GameLocation(
        user = MindustryPlugin.Anuken, repo = MindustryPlugin.Mindustry,
        version = version,
        release = MindustryPlugin.ServerReleaseName
    )

    override fun BE(
        version: String,
    ) = GameLocation(
        MindustryPlugin.Anuken, MindustryPlugin.MindustryBuilds,
        version, "Mindustry-BE-Server-$version.jar"
    )
}