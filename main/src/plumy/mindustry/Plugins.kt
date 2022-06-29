package plumy.mindustry

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownTaskException
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar
import org.gradle.configurationcache.extensions.capitalized
import plumy.dsl.*
import plumy.mindustry.task.*
import java.io.File

class MindustryPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        plugins.apply<MindustryAppPlugin>()
        plugins.apply<MindustryAssetPlugin>()
        plugins.whenHas<JavaPlugin> {
            plugins.apply<MindustryJavaPlugin>()
        }
    }

    companion object {
        const val MindustryTaskGroup = "mindustry"
        const val MindustryAssetTaskGroup = "mindustry assets"
        const val MainExtensionName = "mindustry"
        const val AssetExtensionName = "mindustryAsset"
        const val MindustryDataDirEnv = "MINDUSTRY_DATA_DIR"
        const val DefaultMinGameVersion = "135"
        const val DefaultMindustryVersion = "v135"
        const val DefaultArcVersion = "v135"
        const val Anuken = "anuken"
        const val Mindustry = "mindustry"
        const val MindustryBuilds = "MindustryBuilds"
        const val ClientReleaseName = "Mindustry.jar"
        const val ServerReleaseName = "server-release.jar"
        const val MindustryJitpackRepo = "com.github.anuken.mindustry"
        const val MindustryJitpackMirrorRepo = "com.github.anuken.mindustryjitpack"
        const val ArcJitpackRepo = "com.github.anuken.arc"
        const val MindustryDesktopMainClass = "mindustry.desktop.DesktopLauncher"
        const val MindustrySeverMainClass = "mindustry.server.ServerLauncher"
        val DefaultEmptyFile = File("")
    }
}
/**
 * It transports the Jar task output to running task.
 */
class MindustryJavaPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        val ex = extensions.getOrCreate<MindustryExtension>(
            MindustryPlugin.MainExtensionName
        )
        val dexJar = tasks.register<DexJar>("dexJar") {
            dependsOn("jar")
            group = MindustryPlugin.MindustryTaskGroup
            dependsOn(JavaPlugin.JAR_TASK_NAME)
            classpath.setFrom(
                configurations.compileClasspath,
                configurations.runtimeClasspath
            )
            val jar = tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME)
            jarFiles.setFrom(jar)
        }

        tasks.register<Jar>("deploy") {
            group = MindustryPlugin.MindustryTaskGroup
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
        val main = extensions.getOrCreate<MindustryExtension>(
            MindustryPlugin.MainExtensionName
        )
        val assets = extensions.getOrCreate<MindustryAssetExtension>(
            MindustryPlugin.AssetExtensionName
        )
        val genModHjson = tasks.register<ModHjsonGenerateTask>("genModHjson") {
            group = MindustryPlugin.MindustryTaskGroup
            modMeta.set(main.modMeta)
            outputHjson.set(temporaryDir.resolve("mod.hjson"))
        }
        plugins.whenHas<JavaPlugin> {
            tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
                dependsOn(genModHjson)
                val outputHjson = genModHjson.get().outputHjson.get()
                from(outputHjson)
            }
        }
        // Register this for dynamically configure tasks without class reference in groovy.
        // Eagerly configure this task in order to be added into task group in IDE
        tasks.register<AntiAlias>("antiAlias") {
            group = MindustryPlugin.MindustryTaskGroup
        }.get()
        val genResourceClass = tasks.register<RClassGenerate>("genResourceClass") {
            this.group = MindustryPlugin.MindustryAssetTaskGroup
            val name = assets.qualifiedName.get()
            if (name == "default") {
                val (packageName, _) = main.modMeta.get().main.packageAndClassName()
                qualifiedName.set("$packageName.R")
            } else {
                qualifiedName.set(name)
            }
        }
        target.afterEvaluateThis {
            // Resolve all batches
            val group2Batches = assets.batches.get().resolveBatches()
            var jar: TaskProvider<Jar>? = null
            plugins.whenHas<JavaPlugin> {
                jar = tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME)
            }
            var genResourceClassCounter = 0
            for ((group, batches) in group2Batches) {
                if (batches.isEmpty()) continue
                jar?.configure {
                    batches.forEach { batch ->
                        val dir = batch.dir
                        val root = batch.root
                        if (root == MindustryPlugin.DefaultEmptyFile) {
                            it.from(dir.parentFile) {
                                it.include("${dir.name}/**")
                            }
                        } else { // relative path
                            it.from(root) {
                                it.include("$dir/**")
                            }
                        }
                    }
                }
                if (!batches.any { it.enableGenClass }) continue
                val groupPascal = group.name.lowercase().capitalized()
                val gen = tasks.register<ResourceClassGenerate>("gen${groupPascal}Class") {
                    this.group = MindustryPlugin.MindustryAssetTaskGroup
                    dependsOn(batches.flatMap { it.dependsOn }.distinct().toTypedArray())
                    args.put("ModName", main.modMeta.get().name)
                    generator = assets.getGenerator(group.generator)
                    className.set(group.className)
                    resources.setFrom(batches.filter { it.enableGenClass }.map { it.dir })
                }
                genResourceClass.get().apply {
                    dependsOn(gen)
                    classFiles.from(gen)
                }
                genResourceClassCounter++
            }
            if (genResourceClassCounter > 0) {
                try {
                    tasks.named(JavaPlugin.COMPILE_JAVA_TASK_NAME) {
                        it.dependsOn(genResourceClass)
                    }
                    tasks.named("compileKotlin") {
                        it.dependsOn(genResourceClass)
                    }
                    tasks.named("compileGroovy") {
                        it.dependsOn(genResourceClass)
                    }
                } catch (_: UnknownTaskException) {
                }
            }
        }
    }
}
/**
 * For downloading and running game.
 */
class MindustryAppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val ex = target.extensions.getOrCreate<MindustryExtension>(
            MindustryPlugin.MainExtensionName
        )
        target.afterEvaluateThis {
            // For client side
            val downloadClient = tasks.register<Download>(
                "downloadClient",
            ) {
                group = MindustryPlugin.MindustryTaskGroup
                ex.client.location.get().run {
                    location.set(
                        GitHubDownload.release(
                            user, repo,
                            version, release
                        )
                    )
                    val original = outputFile.get()
                    val targetFile = original.parentFile.resolve(
                        "${original.nameWithoutExtension}-${user}-${repo}-${version}.${original.extension}"
                    )
                    outputFile.set(targetFile)
                }
            }
            // For server side
            val downloadServer = tasks.register<Download>(
                "downloadServer",
            ) {
                group = MindustryPlugin.MindustryTaskGroup
                ex.server.location.get().run {
                    location.set(
                        GitHubDownload.release(
                            user, repo,
                            version, release
                        )
                    )
                    val original = outputFile.get()
                    val targetFile = original.parentFile.resolve(
                        "${original.nameWithoutExtension}-${user}-${repo}-${version}.${original.extension}"
                    )
                    outputFile.set(targetFile)
                }
            }
            val resolveMods = tasks.register<ResolveMods>(
                "resolveMods"
            ) {
                group = MindustryPlugin.MindustryTaskGroup
                mods.set(ex.mods.worksWith)
            }
            val dataDirEx = ex.run.dataDir.get()
            val runClient = tasks.register<RunMindustry>("runClient") {
                group = MindustryPlugin.MindustryTaskGroup
                dependsOn(downloadClient)
                dataDir.set(
                    if (dataDirEx.isNotBlank() && dataDirEx != "temp")
                        File(dataDirEx)
                    else if (dataDirEx == "temp")
                        temporaryDir.resolve("data")
                    else // Default data directory
                        resolveDefaultDataDir()
                )
                mindustryFile.setFrom(downloadClient)
                modsWorkWith.setFrom(resolveMods)
                dataModsPath.set("mods")
                ex.mods.extraModsFromTask.get().forEach {
                    outputtedMods.setFrom(tasks.getByName(it))
                }
            }
            val runServer = tasks.register<RunMindustry>(
                "runServer",
            ) {
                group = MindustryPlugin.MindustryTaskGroup
                dependsOn(downloadServer)
                mainClass.convention(MindustryPlugin.MindustrySeverMainClass)
                mindustryFile.setFrom(downloadServer)
                modsWorkWith.setFrom(resolveMods)
                dataModsPath.convention("config/mods")
                ex.mods.extraModsFromTask.get().forEach {
                    outputtedMods.setFrom(tasks.getByName(it))
                }
            }
        }
    }
}

fun Project.resolveDefaultDataDir(): File {
    return when (getOs()) {
        OS.Unknown -> {
            logger.warn("Can't recognize your operation system.")
            MindustryPlugin.DefaultEmptyFile
        }
        OS.Windows -> FileAt(System.getenv("AppData"), "Mindustry")
        OS.Linux -> FileAt(System.getenv("XDG_DATA_HOME") ?: System.getenv("HOME"), ".local", "share", "Mindustry")
        OS.Mac -> FileAt(System.getenv("HOME"), "Library", "Application Support", "Mindustry")
    }
}