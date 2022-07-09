package io.github.liplum.mindustry

import arc.util.serialization.Jval
import io.github.liplum.dsl.prop
import org.gradle.api.GradleException
import org.gradle.api.Project
import java.net.URL

/**
 * You can configure the dependencies of Mindustry and Arc.
 * **NOTE:** remember to call [mindustryRepo] and [importMindustry] in your `build.gradle(.kts)`
 */
class DependencySpec(
    val target: Project,
) {
    /**
     * Import v135 as default for now.
     * DO NOT trust this behavior, it may change later.
     */
    @InheritFromParent
    @DefaultValue("on \"v135\"")
    val arcDependency = target.prop<IDependency>().apply {
        convention(ArcDependency())
    }
    /**
     * The dependency notation of Mindustry.
     *
     * Default: official "v135"
     */
    @InheritFromParent
    @DefaultValue("on \"v135\"")
    val mindustryDependency = target.prop<IDependency>().apply {
        convention(MindustryDependency())
    }
    val arc = ArcSpec()
    val mindustry = MindustrySpec()
    /**
     * A notation represents the latest version.
     * ## Usages
     * ```
     * mindustry be latest
     * ```
     * This is not recommended, it might not work if you faced the API limit of GitHub.
     */
    val latest: LatestNotation
        get() = LatestNotation
    /**
     * A notation represents the latest release.
     * ## Usages
     * ```
     * mindustry on latestRelease
     * arc on latestRelease
     * ```
     * **Potential Issue** It has a very small chance that it won't work when the new version was just released.
     */
    val latestRelease: LatestReleaseNotation
        get() = LatestReleaseNotation
    /**
     * Fetch the Arc from [arc jitpack](https://github.com/Anuken/Arc).
     */
    fun arc(version: String) {
        arcDependency.set(ArcDependency(version))
    }
    /**
     * Fetch the Mindustry from [mindustry jitpack](https://github.com/Anuken/Mindustry).
     */
    fun mindustry(version: String) {
        mindustryDependency.set(MindustryDependency(version))
    }
    /**
     * Fetch the Mindustry from [mindustry jitpack mirror](https://github.com/Anuken/MindustryJitpack).
     */
    fun mindustryMirror(version: String) {
        mindustryDependency.set(MirrorDependency(version))
    }
    /**
     * Fetch the Arc from [arc jitpack](https://github.com/Anuken/Arc).
     */
    fun arc(map: Map<String, Any>) {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified for `arc`")
        if (version == "latest") {
            arcLatestCommit()
        } else {
            arc(version)
        }
    }
    /**
     * Fetch the Mindustry from [mindustry jitpack](https://github.com/Anuken/Mindustry).
     */
    fun mindustry(map: Map<String, Any>) {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified for `mindustry`")
        if (version == "latest") {
            mindustryLatestRelease()
        } else {
            mindustry(version)
        }
    }
    /**
     * Fetch the dependency of Mindustry from [mindustry jitpack mirror](https://github.com/Anuken/MindustryJitpack).
     */
    fun mindustryMirror(map: Map<String, Any>) {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified for `mindustryMirror`")
        if (version == "latest") {
            mindustryMirrorLatestCommit()
        } else {
            mindustryMirror(version)
        }
    }
    /**
     * Fetch the latest Mindustry from [mindustry jitpack](https://github.com/Anuken/Mindustry).
     *
     * **Potential Issue** It has a very small chance that it won't work when the new version was just released.
     */
    fun mindustryLatestRelease() {
        val latestVersion = target.getLatestVersion("mindustry-release-dependency") {
            try {
                val url = URL(Mgpp.MindustryOfficialReleaseURL)
                val json = Jval.read(url.readText())
                return@getLatestVersion json.getString("tag_name")
            } catch (e: Exception) {
                target.logger.warn("Can't fetch the exact latest version of mindustry, so use ${Mgpp.DefaultMindustryVersion} instead")
                return@getLatestVersion Mgpp.DefaultMindustryVersion
            }
        }
        mindustry(latestVersion)
    }
    /**
     * Fetch the latest [mindustry jitpack mirror](https://github.com/Anuken/MindustryJitpack).
     *
     * **Not Recommended** It may not work due to a network issue or jitpack not yet to build this version
     */
    fun mindustryMirrorLatestCommit() {
        val latestVersion = target.getLatestVersion("mindustry-mirror-commit-dependency") {
            try {
                val url = URL(Mgpp.MindustryJitpackLatestCommit)
                val json = Jval.read(url.readText())
                val fullSha = json.getString("sha")
                return@getLatestVersion fullSha.subSequence(0, 10).toString()
            } catch (e: Exception) {
                target.logger.warn("Can't fetch the exact latest version of mindustry jitpack, so use -SNAPSHOT instead")
                return@getLatestVersion "-SNAPSHOT"
            }
        }
        mindustryMirror(latestVersion)
    }
    /**
     * Fetch the latest Arc from [arc jitpack](https://github.com/Anuken/Arc).
     *
     * **Not Recommended** It may not work due to a network issue or jitpack not yet to build this version
     */
    fun arcLatestCommit() {
        val latestVersion = target.getLatestVersion("arc-commit-dependency") {
            try {
                val url = URL(Mgpp.ArcLatestCommit)
                val json = Jval.read(url.readText())
                val fullSha = json.getString("sha")
                return@getLatestVersion fullSha.subSequence(0, 10).toString()
            } catch (e: Exception) {
                target.logger.warn("Can't fetch the exact latest version of arc, so use -SNAPSHOT instead")
                return@getLatestVersion "-SNAPSHOT"
            }
        }
        arc(latestVersion)
    }
    /**
     * Fetch the latest Arc from [arc jitpack](https://github.com/Anuken/Arc).
     *
     * **Potential Issue** It has a very small chance that it won't work when the new version was just released.
     */
    fun arcLatestTag() {
        val latestVersion = target.getLatestVersion("arc-tag-dependency") {
            try {
                val url = URL(Mgpp.ArcTagURL)
                val json = Jval.read(url.readText())
                val all = json.asArray()
                val latestTag = all.get(0) // the latest tag
                return@getLatestVersion latestTag.getString("name")
            } catch (e: Exception) {
                target.logger.warn("Can't fetch the exact latest version of arc, so use ${Mgpp.DefaultArcVersion} instead")
                return@getLatestVersion Mgpp.DefaultArcVersion
            }
        }
        arc(latestVersion)
    }

    val ArcRepo = Mgpp.ArcJitpackRepo
    val MindustryMirrorRepo = Mgpp.MindustryJitpackMirrorRepo
    val MindustryRepo = Mgpp.MindustryJitpackRepo
    /**
     * Declare an Arc dependency from [arc jitpack](https://github.com/Anuken/Arc).
     */
    fun ArcDependency(
        version: String = Mgpp.DefaultMindustryVersion,
    ) = Dependency(Mgpp.ArcJitpackRepo, version)
    /**
     * Declare a Mindustry dependency from [mindustry jitpack](https://github.com/Anuken/Mindustry).
     */
    fun MindustryDependency(
        version: String = Mgpp.DefaultMindustryVersion,
    ) = Dependency(Mgpp.MindustryJitpackRepo, version)
    /**
     * Declare a dependency.
     */
    fun Dependency(
        fullName: String = "",
        version: String = "",
    ) = io.github.liplum.mindustry.Dependency(fullName, version)
    /**
     * Declare a Mindustry dependency from [mindustry jitpack mirror](https://github.com/Anuken/MindustryJitpack).
     */
    fun MirrorDependency(
        version: String = "",
    ) = MirrorJitpackDependency(Mgpp.MindustryJitpackMirrorRepo, version)
    /**
     * To configure Arc dependency
     */
    inner class ArcSpec {
        /**
         * Fetch the Arc from [arc jitpack](https://github.com/Anuken/Arc).
         */
        infix fun on(version: String) {
            arcDependency.set(ArcDependency(version))
        }
        /**
         * Fetch the Arc from [arc jitpack](https://github.com/Anuken/Arc).
         */
        infix fun on(notation: INotation) {
            if (notation === LatestNotation)
                arcLatestCommit()
            else if (notation === LatestReleaseNotation)
                arcLatestTag()
            else
                throw GradleException("Unknown game notation of mindustry $notation")
        }
        /**
         * Fetch the Arc from [arc jitpack](https://github.com/Anuken/Arc).
         */
        fun on(map: Map<String, Any>) {
            val version = map["version"]?.toString() ?: throw GradleException("No version specified for `arc.on`")
            when (version) {
                "latest" -> on(LatestNotation)
                "latest-release" -> on(LatestReleaseNotation)
                else -> on(version)
            }
        }
    }
    /**
     * To configure Mindustry dependency
     */
    inner class MindustrySpec {
        /**
         * Fetch the dependency of Mindustry from [mindustry jitpack mirror](https://github.com/Anuken/MindustryJitpack).
         */
        infix fun mirror(version: String) {
            mindustryDependency.set(MirrorDependency(version))
        }
        /**
         * Fetch the Mindustry from [mindustry jitpack](https://github.com/Anuken/Mindustry).
         */
        infix fun on(version: String) {
            mindustryDependency.set(MindustryDependency(version))
        }
        /**
         * Fetch the Mindustry from [mindustry jitpack](https://github.com/Anuken/Mindustry).
         */
        infix fun on(notation: INotation) {
            if (notation === LatestNotation || notation === LatestReleaseNotation)
                mindustryLatestRelease()
            else
                throw GradleException("Unknown game notation of mindustry $notation")
        }
        /**
         * Fetch the Mindustry from [mindustry jitpack](https://github.com/Anuken/Mindustry).
         */
        fun on(map: Map<String, Any>) {
            val version = map["version"]?.toString() ?: throw GradleException("No version specified for `mindustry.on`")
            when (version) {
                "latest" -> on(LatestNotation)
                "latest-release" -> on(LatestReleaseNotation)
                else -> on(version)
            }
        }
        /**
         * Fetch the Mindustry from [mindustry jitpack mirror](https://github.com/Anuken/MindustryJitpack).
         */
        infix fun mirror(notation: INotation) {
            if (notation === LatestNotation)
                mindustryMirrorLatestCommit()
            else
                throw GradleException("Unknown game notation of mindustry mirror $notation")
        }
        /**
         * Fetch the Mindustry from [mindustry jitpack mirror](https://github.com/Anuken/MindustryJitpack).
         */
        fun mirror(map: Map<String, Any>) {
            val version = map["version"]?.toString() ?: throw GradleException("No version specified for `mindustry.mirror`")
            when (version) {
                "latest" -> mirror(LatestNotation)
                else -> mirror(version)
            }
        }
    }
}