@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.dsl.afterEvaluateThis
import io.github.liplum.dsl.getOrCreate
import io.github.liplum.dsl.register
import io.github.liplum.mindustry.*
import io.github.liplum.mindustry.LocalProperties.localProperties
import io.github.liplum.mindustry.task.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPlugin
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
            applyNew(target)
            if (false) {
                val runClient = tasks.register<RunMindustry>("runClient") {
                    group = R.taskGroup.mindustry
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
                    val doForciblyClear = project.localProperties.getProperty("mgpp.run.forciblyClear")?.let {
                        it != "false"
                    } ?: ex._run._forciblyClear.get()
                    forciblyClear.set(doForciblyClear)
                    mainClass.convention(R.mainClass.server)
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
    }

    fun applyNew(target: Project) {
        val runX = target.extensions.getOrCreate<RunMindustryExtension>(R.x.runMindustry)
        addResolveModpacks(target, runX)
        addRunClient(target, runX)
        addRunServer(target, runX)
    }

    private fun addResolveModpacks(proj: Project, x: RunMindustryExtension) {
        for (modpack in x.modpacks) {
            val name = modpack.name
            proj.tasks.register<ResolveMods>("resolveModpack$name") {
                group = R.taskGroup.mindustryStuff
                mods.addAll(modpack.mods)
            }
        }
    }

    private fun addRunClient(proj: Project, x: RunMindustryExtension) {
        var anonymous = 0
        for (client in x.clients) {
            val name = client.name.ifEmpty {
                if (anonymous == 0) {
                    anonymous++
                    ""
                } else {
                    (anonymous++ + 1).toString()
                }
            }
            val resolveClient = proj.tasks.register<ResolveGame>("resolveClient$name") {
                group = R.taskGroup.mindustryStuff
                val modpackName = client.modpack
                if (modpackName != null && x.modpacks.any { it.name == modpackName }) {
                    dependsOn("resolveModpack$modpackName")
                }
                location.set(client.location)
            }
            proj.tasks.register<RunClient>("runClient$name") {
                dependsOn(resolveClient)
                group = R.taskGroup.mindustry
                startupArgs.addAll(client.startupArgs)
                dataDir.set(project.dirProv {
                    project.buildDir.resolve("mindustryClientData").resolve(
                        client.name.ifBlank { "Default" }
                    )
                })
                mindustryFile.set(proj.provider {
                    resolveClient.get().outputs.files.singleFile
                })
                val modpack = x.findModpackByName(client.modpack)
                if (modpack != null) {
                    val resolveModpackTask = proj.tasks.named("resolveModpack${modpack.name}")
                    dependsOn(resolveModpackTask)
                    mods.from(resolveModpackTask)
                    for (task in modpack.fromTaskPath) {
                        mods.from(proj.tasks.findByPath(task))
                    }
                    if(proj.plugins.hasPlugin<JavaPlugin>()) {
                        mods.from(proj.tasks.getByPath(JavaPlugin.JAR_TASK_NAME))
                    }
                }
            }
        }
    }

    private fun addRunServer(proj: Project, x: RunMindustryExtension) {
        var anonymous = 0
        for (server in x.servers) {
            val name = server.name.ifEmpty {
                if (anonymous == 0) {
                    anonymous++
                    ""
                } else {
                    (anonymous++ + 1).toString()
                }
            }
            val resolveServer = proj.tasks.register<ResolveGame>("resolveServer$name") {
                group = R.taskGroup.mindustryStuff
                val modpackName = server.modpack
                if (modpackName != null && x.modpacks.any { it.name == modpackName }) {
                    dependsOn("resolveModpack$modpackName")
                }
                location.set(server.location)
            }
            proj.tasks.register<RunServer>("runServer$name") {
                dependsOn(resolveServer)
                group = R.taskGroup.mindustry
                startupArgs.addAll(server.startupArgs)
                dataDir.set(project.dirProv {
                    project.buildDir.resolve("mindustryServerData").resolve(
                        server.name.ifBlank { "Default" }
                    )
                })
                mindustryFile.set(proj.provider {
                    resolveServer.get().outputs.files.singleFile
                })
                val modpack = x.findModpackByName(server.modpack)
                if (modpack != null) {
                    val resolveModpackTask = proj.tasks.named("resolveModpack${modpack.name}")
                    dependsOn(resolveModpackTask)
                    mods.from(resolveModpackTask)
                    for (task in modpack.fromTaskPath) {
                        mods.from(proj.tasks.findByPath(task))
                    }
                    if(proj.plugins.hasPlugin<JavaPlugin>()) {
                        mods.from(proj.tasks.getByPath(JavaPlugin.JAR_TASK_NAME))
                    }
                }
            }
        }
    }
}