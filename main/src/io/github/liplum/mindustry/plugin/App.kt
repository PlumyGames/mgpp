@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.afterEvaluateThis
import io.github.liplum.dsl.getOrCreate
import io.github.liplum.dsl.named
import io.github.liplum.dsl.register
import io.github.liplum.mindustry.*
import io.github.liplum.mindustry.LocalProperties.local
import io.github.liplum.mindustry.LocalProperties.localProperties
import io.github.liplum.mindustry.task.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import java.io.File

/**
 * For downloading and running game.
 */
class MindustryAppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val ex = target.extensions.getOrCreate<MindustryExtension>(
            R.x.mindustry
        )
        val resolveMods = target.tasks.register<ResolveMods>(
            "resolveMods"
        ) {
            group = R.taskGroup.mindustry
            mods.set(ex._mods.worksWith)
        }
        target.tasks.register<CleanMindustrySharedCache>("cleanMindustrySharedCache") {
            group = BasePlugin.BUILD_GROUP
        }
        target.afterEvaluateThis {
            // For client side
            val downloadClient = target.tasks.register<DownloadGame>(
                "downloadClient",
            ) {
                group = R.taskGroup.mindustry
                keepOthers.set(ex._client.keepOtherVersion)
                val localOverwrite = project.local["mgpp.client.location"]
                if (localOverwrite != null)
                    location.set(LocalGameLoc(localOverwrite))
                else location.set(ex._client.location)
            }
            // For server side
            val downloadServer = target.tasks.register<DownloadGame>(
                "downloadServer",
            ) {
                group = R.taskGroup.mindustry
                keepOthers.set(ex._client.keepOtherVersion)
                val localOverwrite = project.local["mgpp.server.location"]
                if (localOverwrite != null)
                    location.set(LocalGameLoc(localOverwrite))
                else location.set(ex._server.location)
            }
            val runClient = tasks.register<RunMindustry>("runClient") {
                group = R.taskGroup.mindustry
                dependsOn(downloadClient)
                mainClass.convention(R.mainClass.desktop)
                val doForciblyClear = project.localProperties.getProperty("mgpp.run.forciblyClear")?.let {
                    it != "false"
                } ?: ex._run._forciblyClear.get()
                forciblyClear.set(doForciblyClear)
                val resolvedDataDir = when (val dataDirConfig =
                    project.localProperties.getProperty("mgpp.run.dataDir").addAngleBracketsIfNeed()
                        ?: ex._run._dataDir.get()
                ) {
                    "<default>" -> resolveDefaultDataDir()
                    "<temp>" -> temporaryDir.resolve("data")
                    "<env>" -> System.getenv(R.env.mindustryDataDir).let {
                        if (it == null) temporaryDir.resolve("data")
                        else File(it).run {
                            if (isFile) this
                            else temporaryDir.resolve("data")
                        }
                    }

                    else -> File(dataDirConfig) // customized data directory
                }

                logger.info("Data directory of $name is $resolvedDataDir .")
                dataDir.set(resolvedDataDir)
                mindustryFile.from(downloadClient)
                modsWorkWith.from(resolveMods)
                dataModsPath.set("mods")
                startupArgs.set(ex._client.startupArgs)
                ex._mods._extraModsFromTask.get().forEach {
                    outputtedMods.from(tasks.getByPath(it))
                }
            }
            val runServer = tasks.register<RunMindustry>(
                "runServer",
            ) {
                group = R.taskGroup.mindustry
                dependsOn(downloadServer)
                val doForciblyClear = project.localProperties.getProperty("mgpp.run.forciblyClear")?.let {
                    it != "false"
                } ?: ex._run._forciblyClear.get()
                forciblyClear.set(doForciblyClear)
                mainClass.convention(R.mainClass.server)
                mindustryFile.from(downloadServer)
                modsWorkWith.from(resolveMods)
                dataModsPath.convention("config/mods")
                startupArgs.set(ex._server.startupArgs)
                ex._mods._extraModsFromTask.get().forEach {
                    dependsOn(tasks.getByPath(it))
                    outputtedMods.from(tasks.getByPath(it))
                }
            }
        }
    }

    fun applyNew(target: Project) {
        val runX = target.extensions.getOrCreate<RunMindustryExtension>(R.x.runMindustry)
        addRunClient(target, runX)
        addRunServer(target, runX)
    }

    private fun addRunClient(proj: Project, x: RunMindustryExtension) {
        var anonymous = 0
        for ((i, client) in x.clients.withIndex()) {
            val name = client.name.ifEmpty {
                if (anonymous == 0) {
                    anonymous++
                    ""
                } else {
                    (anonymous++ + 1).toString()
                }
            }
            proj.tasks.register<RunClient>("runClient$name") {

            }
        }
    }

    private fun addRunServer(proj: Project, x: RunMindustryExtension) {
        var anonymous = 0
        for ((i, server) in x.servers.withIndex()) {

        }
    }
}
/**
 * Provides the existing `downloadClient`: [DownloadGame] task.
 *
 * Because it's registered after project evaluating, please access it in [Project.afterEvaluate].
 */
val TaskContainer.`downloadClient`: TaskProvider<DownloadGame>
    get() = named<DownloadGame>("downloadClient")

/**
 * Provides the existing `downloadServer`: [RunMindustry] task.
 *
 * Because it's registered after project evaluating, please access it in [Project.afterEvaluate].
 */
val TaskContainer.`downloadServer`: TaskProvider<DownloadGame>
    get() = named<DownloadGame>("downloadServer")

/**
 * Provides the existing `runClient`: [RunMindustry] task.
 *
 * Because it's registered after project evaluating, please access it in [Project.afterEvaluate].
 */
val TaskContainer.`runClient`: TaskProvider<RunMindustry>
    get() = named<RunMindustry>("runClient")

/**
 * Provides the existing `runServer`: [RunMindustry] task.
 *
 * Because it's registered after project evaluating, please access it in [Project.afterEvaluate].
 */
val TaskContainer.`runServer`: TaskProvider<RunMindustry>
    get() = named<RunMindustry>("runServer")
