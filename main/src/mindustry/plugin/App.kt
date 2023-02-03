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
    fun applyOld(target: Project) {
        target.afterEvaluateThis {
            // For client side
            /*  val runClient = tasks.register<RunMindustry>("runClient") {
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
                  dataModsPath.set("mods")
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
                  dataModsPath.convention("config/mods")
             }*/
        }
    }

    override fun apply(target: Project) {
        target.tasks.register<CleanMindustrySharedCache>("cleanMindustrySharedCache") {
            group = BasePlugin.BUILD_GROUP
        }
        val runX = target.extensions.getOrCreate<RunMindustryExtension>(R.x.runMindustry)
        target.afterEvaluateThis {
            addResolveModpacks(target, runX)
            addRunClient(target, runX)
            addRunServer(target, runX)
        }
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
        for (client in x.clients) {
            val resolveClient = proj.tasks.register<ResolveGame>("resolveClient${client.name}") {
                group = R.taskGroup.mindustryStuff
                val modpackName = client.modpack
                if (modpackName != null && x.modpacks.any { it.name == modpackName }) {
                    dependsOn("resolveModpack$modpackName")
                }
                location.set(client.location)
            }
            proj.tasks.register<RunClient>("runClient${client.name}") {
                dependsOn(resolveClient)
                group = R.taskGroup.mindustry
                startupArgs.addAll(client.startupArgs)
                dataDir.set(client.dataDir)
                mindustryFile.set(proj.provider {
                    resolveClient.get().outputs.files.singleFile
                })
                val modpack = x.findModpackByName(client.modpack)
                if (modpack != null) {
                    val resolveModpackTask = proj.tasks.named("resolveModpack${modpack.name}")
                    dependsOn(resolveModpackTask)
                    mods.from(resolveModpackTask)
                    for (taskPath in modpack.fromTaskPath) {
                        val task = proj.tasks.findByPath(taskPath)
                        if (task != null) {
                            mods.from(task)
                        }
                    }
                }
                if (proj.plugins.hasPlugin<JavaPlugin>()) {
                    mods.from(proj.tasks.getByPath(JavaPlugin.JAR_TASK_NAME))
                }
            }
        }
    }

    private fun addRunServer(proj: Project, x: RunMindustryExtension) {
        for (server in x.servers) {
            val resolveServer = proj.tasks.register<ResolveGame>("resolveServer$${server.name}") {
                group = R.taskGroup.mindustryStuff
                val modpackName = server.modpack
                if (modpackName != null && x.modpacks.any { it.name == modpackName }) {
                    dependsOn("resolveModpack$modpackName")
                }
                location.set(server.location)
            }
            proj.tasks.register<RunServer>("runServer${server.name}") {
                dependsOn(resolveServer)
                group = R.taskGroup.mindustry
                startupArgs.addAll(server.startupArgs)
                dataDir.set(server.dataDir)
                mindustryFile.set(proj.provider {
                    resolveServer.get().outputs.files.singleFile
                })
                val modpack = x.findModpackByName(server.modpack)
                if (modpack != null) {
                    val resolveModpackTask = proj.tasks.named("resolveModpack${modpack.name}")
                    dependsOn(resolveModpackTask)
                    mods.from(resolveModpackTask)
                    for (taskPath in modpack.fromTaskPath) {
                        val task = proj.tasks.findByPath(taskPath)
                        if (task != null) {
                            mods.from(task)
                        }
                    }
                }
                if (proj.plugins.hasPlugin<JavaPlugin>()) {
                    mods.from(proj.tasks.getByPath(JavaPlugin.JAR_TASK_NAME))
                }
            }
        }
    }
}