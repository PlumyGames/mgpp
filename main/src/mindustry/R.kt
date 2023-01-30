@file:Suppress("ClassName")

package io.github.liplum.mindustry

import java.io.File

object R {
    /**
     * The default check time(ms) for latest version.
     *
     * 1 hour as default.
     */
    const val defaultOutOfDataTime = 1000L * 60 * 60
    /**
     * The check time(ms) for latest version.
     *
     * 1 hour as default.
     */
    var outOfDataTime = defaultOutOfDataTime

    object x {
        /**
         * The name of [MindustryExtension]
         */
        const val mindustry = "mindustry"
        /**
         * The name of [MindustryExtension]
         */
        const val runMindustry = "runMindustry"
        /**
         * The name of [MindustryAssetsExtension]
         */
        const val mindustryAssets = "mindustryAssets"
    }

    object taskGroup {
        /**
         * A task group for main tasks, named `mindustry`
         */
        const val mindustry = "mindustry"
        /**
         * A task group for tasks related to [MindustryAssetsExtension], named `mindustry assets`
         */
        const val mindustryAsset = "mindustry assets"
    }

    /**
     * The environment variables.
     */
    object env {
        /**
         * A folder for Mindustry client to store data.
         */
        const val mindustryDataDir = "MINDUSTRY_DATA_DIR"
    }

    /**
     * The default minGameVersion in `mod.(h)json`.
     *
     * **Note:** You shouldn't pretend this version and work based on it.
     */
    const val DefaultMinGameVersion = "141.3"
    /**
     * [The default Mindustry version](https://github.com/Anuken/Mindustry/releases/tag/v141.3)
     *
     * **Note:** You shouldn't pretend this version and work based on it.
     */
    const val DefaultMindustryVersion = "v141.3"
    /**
     * [The default bleeding edge version](https://github.com/Anuken/MindustryBuilds/releases/tag/23770)
     *
     * **Note:** You shouldn't pretend this version and work based on it.
     */
    const val DefaultMindustryBEVersion = "23770"
    /**
     * [The default Arc version](https://github.com/Anuken/Arc/releases/tag/v141.3)
     *
     * **Note:** You shouldn't pretend this version and work based on it.
     */
    const val DefaultArcVersion = "v141.3"
    /**
     * [Mindustry official release](https://github.com/Anuken/Mindustry/releases)
     */
    const val MindustryOfficialReleaseURL = "https://github.com/Anuken/Mindustry/releases"
    /**
     * GitHub API of [Mindustry official release](https://api.github.com/repos/Anuken/Mindustry/releases/latest)
     */
    const val APIMindustryOfficialReleaseURL = "https://api.github.com/repos/Anuken/Mindustry/releases"
    /**
     * GitHub API of [Latest Mindustry official release](https://api.github.com/repos/Anuken/Mindustry/releases/latest)
     */
    const val APIMindustryOfficialLatestReleaseURL = "https://api.github.com/repos/Anuken/Mindustry/releases/latest"
    /**
     * GitHub API of [Mindustry bleeding-edge release](https://api.github.com/repos/Anuken/Mindustry/releases/latest)
     */
    const val APIMindustryBEReleaseURL = "https://api.github.com/repos/Anuken/MindustryBuilds/releases/latest"
    /**
     * GitHub API of [Latest Mindustry bleeding-edge release](https://api.github.com/repos/Anuken/Mindustry/releases/latest)
     */
    const val APIMindustryBELatestReleaseURL = "https://api.github.com/repos/Anuken/MindustryBuilds/releases/latest"
    /**
     * [Arc tags](https://github.com/Anuken/Arc/tags)
     */
    const val ArcTagURL = "https://api.github.com/repos/Anuken/arc/tags"
    /**
     * [An *Anime* cat](https://github.com/Anuken)
     */
    const val anuken = "anuken"
    /**
     * [Mindustry game](https://github.com/Anuken/Mindustry)
     */
    const val mindustry = "Mindustry"
    /**
     * [Mindustry bleeding-edge](https://github.com/Anuken/MindustryBuilds)
     */
    const val mindustryBuilds = "MindustryBuilds"

    object officialRelease {
        /**
         * [The name convention of client release](https://github.com/Anuken/Mindustry/releases)
         */
        const val client = "Mindustry.jar"
        /**
         * [The name convention of server release](https://github.com/Anuken/Mindustry/releases)
         */
        const val server = "server-release.jar"
    }
    /**
     * [The Mindustry repo on Jitpack](https://github.com/anuken/mindustry)
     */
    const val MindustryJitpackRepo = "com.github.anuken.mindustry"
    /**
     * [The mirror repo of Mindustry on Jitpack](https://github.com/anuken/mindustryjitpack)
     */
    const val MindustryJitpackMirrorRepo = "com.github.anuken.mindustryjitpack"
    /**
     * [The GitHub API to fetch the latest commit of mirror](https://github.com/Anuken/MindustryJitpack/commits/main)
     */
    const val MindustryJitpackLatestCommit = "https://api.github.com/repos/Anuken/MindustryJitpack/commits/main"
    /**
     * [The GitHub API to fetch the latest commit of arc](https://github.com/Anuken/Arc/commits/master)
     */
    const val ArcLatestCommit = "https://api.github.com/repos/Anuken/Arc/commits/master"
    /**
     * [The Arc repo on Jitpack](https://github.com/anuken/arc)
     */
    const val ArcJitpackRepo = "com.github.anuken.arc"

    object mainClass {
        /**
         * The main class of desktop launcher.
         */
        const val desktop = "mindustry.desktop.DesktopLauncher"
        /**
         * The main class of server launcher.
         */
        const val server = "mindustry.server.ServerLauncher"
    }
    /**
     * An empty folder for null-check
     */
    @JvmStatic
    val defaultEmptyFile = File("")

    object fooClient {
        /**
         * The [organization](https://github.com/mindustry-antigrief) of Foo's Client
         */
        const val user = "mindustry-antigrief"
        /**
         * The [Foo's Client repo](https://github.com/mindustry-antigrief/mindustry-client)
         */
        const val repo = "mindustry-client"
    }
}