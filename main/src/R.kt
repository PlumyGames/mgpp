@file:Suppress("ClassName")

package io.github.liplum.mindustry

object R {
    /**
     * The default check time(ms) for latest version.
     *
     * 24 hour by default.
     */
    const val defaultOutOfDataTime = 24 * 60 * 60 * 1000L

    /**
     * The check time(ms) for latest version.
     */
    var outOfDataDuration = defaultOutOfDataTime

    object x {
        /**
         * The name of [MindustryExtension]
         */
        const val mindustry = "mindustry"

        /**
         * The name of [DeployModExtension]
         */
        const val deployMod = "deployMod"

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
        const val mindustryStuff = "mindustry stuff"

        /**
         * A task group for tasks related to [MindustryAssetsExtension], named `mindustry assets`
         */
        const val mindustryAsset = "mindustry assets"
    }

    object task {
        const val zipMod = "zipMod"
        const val genModHjson = "genModHjson"
        const val deployMod = "deployMod"
        const val dexJar = "dexJar"
        const val antiAlias = "antiAlias"
        const val cleanMindustrySharedCache = "cleanMindustrySharedCache"
        const val resolveMods = "resolveMods"
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

    object modMeta {
        /**
         * The default minGameVersion in `mod.(h)json`.
         *
         * **Note:** You shouldn't pretend this version and work based on it.
         */
        const val defaultMinGameVersion = "146"
    }

    object version {

        /**
         * [The default Mindustry version](https://github.com/Anuken/Mindustry/releases)
         *
         * **Note:** You shouldn't pretend this version and work based on it.
         */
        const val defaultOfficial = "v146"

        /**
         * [The default bleeding edge version](https://github.com/Anuken/MindustryBuilds/releases)
         *
         * **Note:** You shouldn't pretend this version and work based on it.
         */
        const val defaultBE = "24664"

        /**
         * [The default Arc version](https://github.com/Anuken/Arc/releases/tag)
         *
         * **Note:** You shouldn't pretend this version and work based on it.
         */
        const val defaultArc = "v146"
    }

    object github {
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

        object tag {
            /**
             * [Mindustry official release](https://github.com/Anuken/Mindustry/releases)
             */
            const val release = "https://github.com/Anuken/Mindustry/releases"

            /**
             * GitHub API of [Mindustry official release](https://api.github.com/repos/Anuken/Mindustry/releases/latest)
             */
            const val releaseAPI = "https://api.github.com/repos/Anuken/Mindustry/releases"

            /**
             * GitHub API of [Latest Mindustry official release](https://api.github.com/repos/Anuken/Mindustry/releases/latest)
             */
            const val latestReleaseAPI = "https://api.github.com/repos/Anuken/Mindustry/releases/latest"

            /**
             * GitHub API of [Mindustry bleeding-edge release](https://api.github.com/repos/Anuken/Mindustry/releases/latest)
             */
            const val beReleaseAPI = "https://api.github.com/repos/Anuken/MindustryBuilds/releases/latest"

            /**
             * GitHub API of [Latest Mindustry bleeding-edge release](https://api.github.com/repos/Anuken/Mindustry/releases/latest)
             */
            const val beLatestReleaseAPI = "https://api.github.com/repos/Anuken/MindustryBuilds/releases/latest"

            /**
             * [Arc tags](https://github.com/Anuken/Arc/tags)
             */
            const val arc = "https://api.github.com/repos/Anuken/arc/tags"

            /**
             * [The GitHub API to fetch the latest commit of mirror](https://github.com/Anuken/MindustryJitpack/commits/main)
             */
            const val mirrorLatestCommit = "https://api.github.com/repos/Anuken/MindustryJitpack/commits/main"

            /**
             * [The GitHub API to fetch the latest release of mirror](https://github.com/Anuken/MindustryJitpack/releases/latest)
             */
            const val mirrorLatestRelase = "https://api.github.com/repos/Anuken/MindustryJitpack/releases/latest"

            /**
             * [The GitHub API to fetch the latest commit of arc](https://github.com/Anuken/Arc/commits/master)
             */
            const val arcLatestCommit = "https://api.github.com/repos/Anuken/Arc/commits/master"
        }

        object jitpack {
            /**
             * [The Mindustry repo on Jitpack](https://github.com/anuken/mindustry)
             */
            const val official = "com.github.anuken.mindustry"

            /**
             * [The mirror repo of Mindustry on Jitpack](https://github.com/anuken/mindustryjitpack)
             */
            const val mirror = "com.github.anuken.mindustryjitpack"

            /**
             * [The Arc repo on Jitpack](https://github.com/anuken/arc)
             */
            const val arc = "com.github.anuken.arc"
        }
    }

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

    object beRelease {
        fun client(version: String) = "Mindustry-BE-Desktop-$version.jar"
        fun client() = "Mindustry-BE-Desktop.jar"
        fun server(version: String) = "Mindustry-BE-Server-$version.jar"
        fun server() = "Mindustry-BE-Server.jar"
    }

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

    object cnARC {
        /**
         * The [maintainer](https://github.com/Jackson11500) of CN-ARC Client
         */
        const val user = "Jackson11500"

        /**
         * The [CN-ARC client buildings](https://github.com/Jackson11500/Mindustry-CN-ARC-Builds)
         */
        const val repo = "Mindustry-CN-ARC-Builds"
    }
}