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
    val arcDependency = target.prop<IDependency>().apply {
        convention(ArcDependency())
    }
    /**
     * Import v135 as default for now.
     * DO NOT trust this behavior, it may change later.
     */
    val mindustryDependency = target.prop<IDependency>().apply {
        convention(MindustryDependency())
    }
    val arc = ArcSpec()
    val mindustry = MindustrySpec()
    val latest: LatestNotation
        get() = LatestNotation

    fun arc(version: String) {
        arcDependency.set(ArcDependency(version))
    }

    fun mindustry(version: String) {
        mindustryDependency.set(MindustryDependency(version))
    }
    /**
     * Fetch the dependency of Mindustry from [mindustry jitpack mirror](https://github.com/Anuken/MindustryJitpack).
     */
    fun mindustryMirror(version: String) {
        mindustryDependency.set(MirrorDependency(version))
    }

    fun arc(map: Map<String, Any>) {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified for `arc`")
        if (version == "latest") {
            arcLatest()
        } else {
            arc(version)
        }
    }

    fun mindustry(map: Map<String, Any>) {
        val version = map["version"]?.toString() ?: throw GradleException("No version specified for `mindustry`")
        if (version == "latest") {
            mindustryLatest()
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
            mindustryMirrorLatest()
        } else {
            mindustryMirror(version)
        }
    }

    fun mindustryLatest() {
        try {
            val url = URL(MindustryPlugin.OfficialReleaseURL)
            val json = Jval.read(url.readText())
            val version = json.getString("tag_name")
            mindustry(version = version)
        } catch (e: Exception) {
            target.logger.warn("Can't fetch the exact latest version of mindustry, so use ${MindustryPlugin.DefaultMindustryVersion} instead")
            mindustryMirror(version = MindustryPlugin.DefaultMindustryVersion)
        }
    }
    /**
     * Fetch the latest [mindustry jitpack mirror](https://github.com/Anuken/MindustryJitpack).
     *
     * **Not Recommended**, it may not work due to a network issue or jitpack not yet to build this version
     */
    fun mindustryMirrorLatest() {
        try {
            val url = URL(MindustryPlugin.MindustryJitpackLatestCommit)
            val json = Jval.read(url.readText())
            val fullSha = json.getString("sha")
            val shortSha = fullSha.subSequence(0, 10).toString()
            mindustryMirror(version = shortSha)
        } catch (e: Exception) {
            target.logger.warn("Can't fetch the exact latest version of mindustry jitpack, so use -SNAPSHOT instead")
            mindustryMirror(version = "-SNAPSHOT")
        }
    }

    fun arcLatest() {
        try {
            val url = URL(MindustryPlugin.ArcLatestCommit)
            val json = Jval.read(url.readText())
            val fullSha = json.getString("sha")
            val shortSha = fullSha.subSequence(0, 10).toString()
            arc(version = shortSha)
        } catch (e: Exception) {
            target.logger.warn("Can't fetch the exact latest version of arc, so use -SNAPSHOT instead")
            arc(version = "-SNAPSHOT")
        }
    }

    val ArcRepo = MindustryPlugin.ArcJitpackRepo
    val MindustryMirrorRepo = MindustryPlugin.MindustryJitpackMirrorRepo
    val MindustryRepo = MindustryPlugin.MindustryJitpackRepo
    fun ArcDependency(
        version: String = MindustryPlugin.DefaultMindustryVersion,
    ) = Dependency(MindustryPlugin.ArcJitpackRepo, version)

    fun MindustryDependency(
        version: String = MindustryPlugin.DefaultMindustryVersion,
    ) = Dependency(MindustryPlugin.MindustryJitpackRepo, version)

    fun Dependency(
        fullName: String = "",
        version: String = "",
    ) = io.github.liplum.mindustry.Dependency(fullName, version)
    /**
     * Fetch the dependency of Mindustry from [mindustry jitpack mirror](https://github.com/Anuken/MindustryJitpack).
     */
    fun MirrorDependency(
        version: String = "",
    ) = MirrorJitpackDependency(MindustryPlugin.MindustryJitpackMirrorRepo, version)

    inner class ArcSpec {
        infix fun on(version: String) {
            arcDependency.set(ArcDependency(version))
        }

        infix fun on(notation: IMgppNotation) {
            if (notation === LatestNotation)
                arcLatest()
            else
                throw GradleException("Unknown game notation of mindustry $notation")
        }

        fun on(map: Map<String, Any>) {
            val version = map["version"]?.toString() ?: throw GradleException("No version specified for `arc.on`")
            arcDependency.set(ArcDependency(version))
        }
    }

    inner class MindustrySpec {
        /**
         * Fetch the dependency of Mindustry from [mindustry jitpack mirror](https://github.com/Anuken/MindustryJitpack).
         */
        infix fun mirror(version: String) {
            mindustryDependency.set(MirrorDependency(version))
        }

        infix fun on(version: String) {
            mindustryDependency.set(MindustryDependency(version))
        }

        infix fun on(notation: IMgppNotation) {
            if (notation === LatestNotation)
                mindustryLatest()
            else
                throw GradleException("Unknown game notation of mindustry $notation")
        }
        /**
         * Fetch the dependency of Mindustry from [mindustry jitpack mirror](https://github.com/Anuken/MindustryJitpack).
         */
        infix fun mirror(notation: IMgppNotation) {
            if (notation === LatestNotation)
                mindustryMirrorLatest()
            else
                throw GradleException("Unknown game notation of mindustry $notation")
        }
        /**
         * Fetch the dependency of Mindustry from [mindustry jitpack mirror](https://github.com/Anuken/MindustryJitpack).
         */
        fun mirror(map: Map<String, Any>) {
            val version = map["version"]?.toString() ?: throw GradleException("No version specified for `mindustry.mirror`")
            mindustryDependency.set(MirrorDependency(version))
        }

        fun on(map: Map<String, Any>) {
            val version = map["version"]?.toString() ?: throw GradleException("No version specified for `mindustry.on`")
            mindustryDependency.set(MindustryDependency(version))
        }
    }
}