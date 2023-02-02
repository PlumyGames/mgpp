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

class Server : Common() {
}

class AddServerSpec(
    override val proj: Project,
    override val backend: Server
) : AddCommonSpec<Server>() {

    override fun official(version: String) {
        github(
            user = R.anuken,
            repo = R.mindustry,
            tag = version,
            file = R.officialRelease.server,
        )
    }

    override fun official(version: Notation) {
        when (version) {
            Notation.latest -> LatestOfficialMindustryLoc(file = R.officialRelease.server).checkAndSet()
            else -> proj.logger.log(LogLevel.WARN, "Version \"$version\" is unsupported")
        }
    }

    override fun be(version: String) {
        github(
            user = R.anuken,
            repo = R.mindustryBuilds,
            tag = version,
            file = "Mindustry-BE-Server-$version.jar",
        )
    }

    override fun be(version: Notation) {
        when (version) {
            Notation.latest -> LatestBeMindustryLoc(file = "Mindustry-BE-Server-$version.jar").checkAndSet()
            else -> proj.logger.log(LogLevel.WARN, "Version \"$version\" is unsupported")
        }
    }
}
