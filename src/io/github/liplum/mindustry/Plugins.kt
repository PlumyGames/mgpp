package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.mindustry.task.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.configurationcache.extensions.capitalized
import java.io.File

class MindustryPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        try {
            plugins.apply<JavaPlugin>()
        } catch (e: Exception) {
            logger.warn("Your project doesn't support java plugin, so mgpp was disabled.", e)
            return@func
        }
        plugins.apply<MindustryAppPlugin>()
        plugins.apply<MindustryAssetPlugin>()
        plugins.apply<MindustryJavaPlugin>()
    }

    companion object {
        const val MindustryTaskGroup = "mindustry"
        const val MindustryAssetTaskGroup = "mindustry assets"
        const val MainExtensionName = "mindustry"
        const val AssetExtensionName = "mindustryAssets"
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
        @JvmStatic
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
        target.afterEvaluateThis {
            if (ex.deploy.enableFatJar.get()) {
                tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
                    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                    from(
                        configurations.runtimeClasspath.get().map {
                            if (it.isDirectory) it else zipTree(it)
                        }
                    )
                }
            }
        }
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
            sdkRoot.set(ex.deploy._androidSdkRoot)
        }
        val modMeta = ex.modMeta.get()
        ex.deploy._baseName.convention(provider {
            modMeta.name
        })
        ex.deploy._version.convention(provider {
            modMeta.version
        })
        tasks.register<Jar>("deploy") {
            group = MindustryPlugin.MindustryTaskGroup
            val jar = tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME)
            dependsOn(jar)
            dependsOn(dexJar)
            destinationDirectory.set(temporaryDir)
            archiveBaseName.set(ex.deploy._baseName)
            archiveVersion.set(ex.deploy._version)
            archiveClassifier.set(ex.deploy._classifier)
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
        val assets = extensions.getOrCreate<MindustryAssetsExtension>(
            MindustryPlugin.AssetExtensionName
        )
        val genModHjson = tasks.register<ModHjsonGenerateTask>("genModHjson") {
            group = MindustryPlugin.MindustryTaskGroup
            modMeta.set(main.modMeta)
            outputHjson.set(temporaryDir.resolve("mod.hjson"))
        }
        tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
            dependsOn(genModHjson)
            from(genModHjson)
        }
        // Register this for dynamically configure tasks without class reference in groovy.
        // Eagerly configure this task in order to be added into task group in IDE
        tasks.register<AntiAlias>("antiAlias") {
            group = MindustryPlugin.MindustryTaskGroup
        }.get()
        // Doesn't register the tasks if no resource needs to generate its class.
        val genResourceClass by lazy {
            tasks.register<RClassGenerate>("genResourceClass") {
                this.group = MindustryPlugin.MindustryAssetTaskGroup
                val name = assets.qualifiedName.get()
                if (name == "default") {
                    val modMeta = main.modMeta.get()
                    val (packageName, _) = modMeta.main.packageAndClassName()
                    qualifiedName.set("$packageName.R")
                } else {
                    qualifiedName.set(name)
                }
            }
        }
        target.afterEvaluateThis {
            val assetsRoot = assets.assetsRoot.get()
            if (assetsRoot != MindustryPlugin.DefaultEmptyFile) {
                tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
                    from(assetsRoot)
                }
            }
            val icon = assets.icon.get()
            tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
                from(icon)
            }
            // Resolve all batches
            val group2Batches = assets.batches.get().resolveBatches()
            val jar = tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME)
            var genResourceClassCounter = 0
            for ((type, batches) in group2Batches) {
                if (batches.isEmpty()) continue
                jar.configure {
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
                val groupPascal = type.group.lowercase().capitalized()
                val gen = tasks.register<ResourceClassGenerate>("gen${groupPascal}Class") {
                    this.group = MindustryPlugin.MindustryAssetTaskGroup
                    dependsOn(batches.flatMap { it.dependsOn }.distinct().toTypedArray())
                    args.put("ModName", main.modMeta.get().name)
                    args.put("NameRule", type.nameRule.name)
                    args.putAll(assets.args)
                    generator.set(type.generator)
                    className.set(type.className)
                    resources.setFrom(batches.filter { it.enableGenClass }.map { it.dir })
                }
                genResourceClass.get().apply {
                    dependsOn(gen)
                    classFiles.from(gen)
                }
                genResourceClassCounter++
            }
            if (genResourceClassCounter > 0) {
                safeRun {
                    tasks.named(JavaPlugin.COMPILE_JAVA_TASK_NAME) {
                        it.dependsOn(genResourceClass)
                    }
                }
                safeRun {
                    tasks.named("compileKotlin") {
                        it.dependsOn(genResourceClass)
                    }
                }
                safeRun {
                    tasks.named("compileGroovy") {
                        it.dependsOn(genResourceClass)
                    }
                }
            }
        }
    }
}

inline fun safeRun(func: () -> Unit) {
    try {
        func()
    } catch (_: Throwable) {
    }
}
/**
 * For downloading and running game.
 */
class MindustryAppPlugin : Plugin<Project> {
    override fun apply(target: Project)  {
        val ex = target.extensions.getOrCreate<MindustryExtension>(
            MindustryPlugin.MainExtensionName
        )
        val resolveMods = target.tasks.register<ResolveMods>(
            "resolveMods"
        ) {
            group = MindustryPlugin.MindustryTaskGroup
            mods.set(ex.mods.worksWith)
        }
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
            val dataDirEx = ex.run._dataDir.get()
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
                ex.mods._extraModsFromTask.get().forEach {
                    outputtedMods.from(tasks.getByName(it))
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
                ex.mods._extraModsFromTask.get().forEach {
                    outputtedMods.from(tasks.getByPath(it))
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