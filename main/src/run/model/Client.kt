@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import org.gradle.api.Project

class Client(
    name: String, isAnonymous: Boolean,
) : GameSide(name, isAnonymous, GameSideType.Client)

class AddClientSpec(
    override val proj: Project,
    override val backend: Client,
) : AddGameSideSpec<Client>() {
    fun useDefaultDataDir() {
        dataDir = MindustryDefaultDataDirLoc
    }

    override fun official(version: String) {
        github(
            user = R.github.anuken,
            repo = R.github.mindustry,
            tag = version,
            file = R.officialRelease.client,
        )
    }

    override fun official(version: Notation) {
        when (version) {
            Notation.latest -> LatestOfficialMindustryLoc(MindustryEnd.Client).checkAndSet()
            else -> proj.logger.error("Version \"$version\" is unsupported")
        }
    }

    override fun be(version: String) {
        github(
            user = R.github.anuken,
            repo = R.github.mindustryBuilds,
            tag = version,
            file = "Mindustry-BE-Desktop-$version.jar",
        )
    }

    override fun be(version: Notation) {
        when (version) {
            Notation.latest -> LatestMindustryBELoc(MindustryEnd.Client).checkAndSet()
            else -> proj.logger.error("Version \"$version\" is unsupported")
        }
    }

    /**
     * ### Kotlin DSL
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
     * ### Groovy DSL
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

    /**
     * ### Kotlin DSL
     * ```kotlin
     * cnARC(
     *   tag="30729",
     *   file="Mindustry-CN-ARC-Desktop-$version.jar"
     * )
     * ```
     */
    fun cnARC(
        tag: String,
        file: String,
    ) {
        github(
            user = R.cnARC.user,
            repo = R.cnARC.repo,
            tag = tag,
            file = file,
        )
    }

    /**
     * ### Kotlin DSL
     * ```kotlin
     * cnARC(version="30729")
     * ```
     */
    fun cnARC(version: String) {
        github(
            user = R.cnARC.user,
            repo = R.cnARC.repo,
            tag = version,
            file = "Mindustry-CN-ARC-Desktop-$version.jar",
        )
    }

    /**
     * ### Kotlin DSL
     * ```kotlin
     * cnARC(version=30729)
     * ```
     */
    fun cnARC(version: Int) {
        cnARC(version = version.toString())
    }

    /**
     * ### Groovy DSL
     * ```groovy
     * cnARC version: 30388
     * cnARC version: "30388"
     * cnARC tag: "30388", file: "Mindustry-CN-ARC-Desktop-30729.jar"
     * ```
     */
    fun cnARC(props: Map<String, String>) {
        val version = props["version"]
        if (version != null) {
            cnARC(version = version.toString())
        } else {
            cnARC(
                tag = props["tag"] ?: "",
                file = props["file"] ?: "",
            )
        }
    }
}
