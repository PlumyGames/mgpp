package plumy.mindustry

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Jar
import plumy.dsl.*
import plumy.mindustry.task.DexJar
import plumy.mindustry.task.DownloadTask
import plumy.mindustry.task.ModHjsonGenerateTask
import plumy.mindustry.task.RunMindustryTask
import java.io.File

class MindustryPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        plugins.apply<MindustryAppPlugin>()
        plugins.apply<MindustryAssetPlugin>()
        plugins.whenHas<JavaPlugin> {
            plugins.apply<MindustryJavaPlugin>()
        }
    }
}
/**
 * It transports the Jar task output to running task.
 */
class MindustryJavaPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        val ex = extensions.getOrCreate<MindustryExtension>(
            Meta.ExtensionName
        )
        tasks.withType<RunMindustryTask> {
            outputMods.setFrom(
                tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME).get(),
                *ex.mods.extraModsFromTask.get().map {
                    tasks.named(it)
                }.toTypedArray()
            )
        }
        val dexJar = tasks.register<DexJar>("dexJar") {
            dependsOn("jar")
            group = Meta.TaskGroup
            dependsOn(JavaPlugin.JAR_TASK_NAME)
            classpath.setFrom(
                configurations.compileClasspath,
                configurations.runtimeClasspath
            )
            val jar = tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME)
            jarFiles.setFrom(jar.get())
        }

        tasks.register<Jar>("deploy") {
            group = Meta.TaskGroup
            dependsOn("jar")
            dependsOn("dexJar")
            val jar = tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME)
            destinationDirectory.set(temporaryDir)
            archiveFileName.set("deploy.jar")
            from(
                *jar.get().outputs.files.map { project.zipTree(it) }.toTypedArray(),
                *dexJar.get().outputs.files.map { project.zipTree(it) }.toTypedArray(),
            )
        }
    }
}

class MindustryAssetPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        val ex = extensions.getOrCreate<MindustryExtension>(
            Meta.ExtensionName
        )
        val genModHjson = tasks.register<ModHjsonGenerateTask>("genModHjson") {
            group = Meta.TaskGroup
            modMeta.set(ex.assets.modMeta)
            outputHjson.set(temporaryDir.resolve("mod.hjson"))
        }
        tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
            dependsOn(genModHjson)
            val outputHjson = genModHjson.get().outputHjson.get()
            from(outputHjson)
        }
    }
}
/**
 * For downloading and running game.
 */
class MindustryAppPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        val ex = extensions.getOrCreate<MindustryExtension>(
            Meta.ExtensionName
        )
        // For client side
        val downloadClient = tasks.register<DownloadTask>(
            "downloadClient",
        ) {
            group = Meta.TaskGroup
            ex.clientLocation.get().run {
                assets.set(
                    GitHubAsset.release(
                        user, repo,
                        version, releaseName
                    )
                )
                val original = outputPath.get()
                val targetFile = original.parentFile.resolve(
                    "${original.nameWithoutExtension}-${user}-${repo}-${version}.${original.extension}"
                )
                outputPath.set(targetFile)
            }
        }
        val runClient = tasks.register<RunMindustryTask>(
            "runClient",
        ) {
            group = Meta.TaskGroup
            mainClass.convention(Meta.MindustryDesktopMainClass)
            classPath.setFrom(downloadClient.get())
            dataModsPath.convention("mods")
        }
        // For server side
        val downloadServer = tasks.register<DownloadTask>(
            "downloadServer",
        ) {
            group = Meta.TaskGroup
            ex.severLocation.get().run {
                assets.set(
                    GitHubAsset.release(
                        user, repo,
                        version, releaseName
                    )
                )
                val original = outputPath.get()
                val targetFile = original.parentFile.resolve(
                    "${original.nameWithoutExtension}-${user}-${repo}-${version}.${original.extension}"
                )
                outputPath.set(targetFile)
            }
        }
        val runServer = tasks.register<RunMindustryTask>(
            "runServer",
        ) {
            group = Meta.TaskGroup
            mainClass.convention(Meta.MindustrySeverMainClass)
            classPath.setFrom(downloadServer.get())
            dataModsPath.convention("config/mods")
        }
    }
}