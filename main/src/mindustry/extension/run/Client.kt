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

class Client : Common()

class AddClientSpec(
    override val proj: Project,
    override val backend: Client,
) : AddCommonSpec<Client>() {

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
            Notation.latest -> LatestOfficialMindustryLoc(file = R.officialRelease.client).checkAndSet()
            else -> proj.logger.log(LogLevel.WARN, "Version \"$version\" is unsupported")
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
            Notation.latest -> LatestBeMindustryLoc(file = "Mindustry-BE-Desktop-$version.jar").checkAndSet()
            else -> proj.logger.log(LogLevel.WARN, "Version \"$version\" is unsupported")
        }
    }
    /**
     * [Foo's client](https://github.com/mindustry-antigrief/mindustry-client)
     *
     * ```kotlin
     * fooClient (
     *   tag = "v8.0.0",
     *   file = "erekir-client.jar"
     * )
     * ```
     */
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
    /**
     * [Foo's client](https://github.com/mindustry-antigrief/mindustry-client)
     *
     * ```groovy
     * fooClient tag: "v8.0.0", file: "erekir-client.jar"
     * ```
     */
    fun fooClient(props: Map<String, String>) {
        fooClient(
            tag = props["tag"] ?: "",
            file = props["file"] ?: "",
        )
    }
}
