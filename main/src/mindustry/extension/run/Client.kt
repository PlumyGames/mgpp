@file:JvmMultifileClass
@file:JvmName("ExtensionKt")
@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.mindustry.*
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.ExtensionAware
import java.io.File

class Client : Common() {
    /** @see [AddClientSpec.dataDir] */
    var dataDir: String? = null
}

class AddClientSpec(
    override val proj: Project,
    override val backend: Client,
) : AddCommonSpec<Client>() {
    /**
     * *Optional*
     * The name of Mindustry's data directory where to put saves.
     *
     * The default [dataDir] is the same as [name].
     */
    var dataDir: String?
        get() = backend.dataDir
        set(value) {
            backend.dataDir = value
        }

    override fun official(version: String) {
        github(
            user = R.anuken,
            repo = R.mindustry,
            tag = version,
            file = R.officialRelease.client,
        )
    }

    override fun official(version: Notation) {
        when (version) {
            Notation.latest -> backend.location = LatestOfficialMindustryLoc(file = R.officialRelease.client)
            else -> proj.logger.log(LogLevel.WARN, "Version $version is unsupported")
        }
    }

    override fun be(version: String) {
        github(
            user = R.anuken,
            repo = R.mindustryBuilds,
            tag = version,
            file = "Mindustry-BE-Desktop-$version.jar",
        )
    }

    override fun be(version: Notation) {
        when (version) {
            Notation.latest -> backend.location = LatestBeMindustryLoc(file = "Mindustry-BE-Desktop-$version.jar")
            else -> proj.logger.log(LogLevel.WARN, "Version $version is unsupported")
        }
    }

    fun fooClient(
        tag: String,
        file: String,
    ) {
        github(
            user = R.fooClient.user,
            repo = R.fooClient.repo,
            tag = tag,
            file = file,
        )
    }

    fun fooClient(props: Map<String, String>) {
        fooClient(
            tag = props["tag"] ?: "",
            file = props["file"] ?: "",
        )
    }
}
