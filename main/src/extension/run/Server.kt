@file:JvmMultifileClass
@file:JvmName("ExtensionKt")
@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.mindustry.*
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

class Server(name: String, isAnonymous: Boolean) : Common(name, isAnonymous)

class AddServerSpec(
    override val proj: Project,
    override val backend: Server
) : AddCommonSpec<Server>() {

    override fun official(version: String) {
        github(
            user = R.github.anuken,
            repo = R.github.mindustry,
            tag = version,
            file = R.officialRelease.server,
        )
    }

    override fun official(version: Notation) {
        when (version) {
            Notation.latest -> LatestOfficialMindustryLoc(MindustryEnd.Server).checkAndSet()
            else -> proj.logger.log(LogLevel.WARN, "Version \"$version\" is unsupported")
        }
    }

    override fun be(version: String) {
        github(
            user = R.github.anuken,
            repo = R.github.mindustryBuilds,
            tag = version,
            file = "Mindustry-BE-Server-$version.jar",
        )
    }

    override fun be(version: Notation) {
        when (version) {
            Notation.latest -> LatestMindustryBELoc(MindustryEnd.Server).checkAndSet()
            else -> proj.logger.log(LogLevel.WARN, "Version \"$version\" is unsupported")
        }
    }
}
