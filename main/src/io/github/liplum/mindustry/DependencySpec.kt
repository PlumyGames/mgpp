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
            arcLatest()
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
    /**
     * Fetch the latest Mindustry from [mindustry jitpack](https://github.com/Anuken/Mindustry).
     */
    fun mindustryLatest() {
        try {
            val url = URL(Mgpp.OfficialReleaseURL)
            val json = Jval.read(url.readText())
            val version = json.getString("tag_name")
            mindustry(version = version)
        } catch (e: Exception) {
            target.logger.warn("Can't fetch the exact latest version of mindustry, so use ${Mgpp.DefaultMindustryVersion} instead")
            mindustryMirror(version = Mgpp.DefaultMindustryVersion)
        }
    }
    /**
     * Fetch the latest [mindustry jitpack mirror](https://github.com/Anuken/MindustryJitpack).
     *
     * **Not Recommended**, it may not work due to a network issue or jitpack not yet to build this version
     */
    fun mindustryMirrorLatest() {
        try {
            val url = URL(Mgpp.MindustryJitpackLatestCommit)
            val json = Jval.read(url.readText())
            val fullSha = json.getString("sha")
            val shortSha = fullSha.subSequence(0, 10).toString()
            mindustryMirror(version = shortSha)
        } catch (e: Exception) {
            target.logger.warn("Can't fetch the exact latest version of mindustry jitpack, so use -SNAPSHOT instead")
            mindustryMirror(version = "-SNAPSHOT")
        }
    }
    /**
     * Fetch the latest Arc from [arc jitpack](https://github.com/Anuken/Arc).
     */
    fun arcLatest() {
        try {
            val url = URL(Mgpp.ArcLatestCommit)
            val json = Jval.read(url.readText())
            val fullSha = json.getString("sha")
            val shortSha = fullSha.subSequence(0, 10).toString()
            arc(version = shortSha)
        } catch (e: Exception) {
            target.logger.warn("Can't fetch the exact latest version of arc, so use -SNAPSHOT instead")
            arc(version = "-SNAPSHOT")
        }
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
                arcLatest()
            else
                throw GradleException("Unknown game notation of mindustry $notation")
        }
        /**
         * Fetch the Arc from [arc jitpack](https://github.com/Anuken/Arc).
         */
        fun on(map: Map<String, Any>) {
            val version = map["version"]?.toString() ?: throw GradleException("No version specified for `arc.on`")
            if (version == "latest") {
                on(LatestNotation)
            } else {
                on(version)
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
            if (notation === LatestNotation)
                mindustryLatest()
            else
                throw GradleException("Unknown game notation of mindustry $notation")
        }
        /**
         * Fetch the Mindustry from [mindustry jitpack](https://github.com/Anuken/Mindustry).
         */
        fun on(map: Map<String, Any>) {
            val version = map["version"]?.toString() ?: throw GradleException("No version specified for `mindustry.on`")
            if (version == "latest") {
                on(LatestNotation)
            } else {
                on(version)
            }
        }
        /**
         * Fetch the Mindustry from [mindustry jitpack mirror](https://github.com/Anuken/MindustryJitpack).
         */
        infix fun mirror(notation: INotation) {
            if (notation === LatestNotation)
                mindustryMirrorLatest()
            else
                throw GradleException("Unknown game notation of mindustry $notation")
        }
        /**
         * Fetch the Mindustry from [mindustry jitpack mirror](https://github.com/Anuken/MindustryJitpack).
         */
        fun mirror(map: Map<String, Any>) {
            val version = map["version"]?.toString() ?: throw GradleException("No version specified for `mindustry.mirror`")
            if (version == "latest") {
                mirror(LatestNotation)
            } else {
                mirror(version)
            }
        }
    }
}