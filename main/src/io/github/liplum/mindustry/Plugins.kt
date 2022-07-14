@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.mindustry.LocalProperties.localProperties
import io.github.liplum.mindustry.task.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Jar
import org.gradle.configurationcache.extensions.capitalized
import java.io.File

typealias Mgpp = MindustryPlugin

class MindustryPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        LocalProperties.clearCache(this)
        val ex = target.extensions.getOrCreate<MindustryExtension>(
            Mgpp.MainExtensionName
        )
        /**
         * Handle [InheritFromParent].
         * Because they're initialized at the [Plugin.apply] phase, the user-code will overwrite them if it's possible.
         */
        target.parent?.let {
            it.plugins.whenHas<MindustryPlugin> {
                val parentEx = it.extensions.getOrCreate<MindustryExtension>(Mgpp.MainExtensionName)
                ex._dependency.mindustryDependency.set(parentEx._dependency.mindustryDependency)
                ex._dependency.arcDependency.set(parentEx._dependency.arcDependency)
                ex._client.location.set(parentEx._client.location)
                ex._server.location.set(parentEx._server.location)
                ex._run._dataDir.set(parentEx._run._dataDir)
                ex._run._forciblyClear.set(parentEx._run._forciblyClear)
                ex._deploy._androidSdkRoot.set(parentEx._deploy._androidSdkRoot)
            }
        }
        plugins.apply<MindustryAppPlugin>()
        plugins.apply<MindustryAssetPlugin>()
        plugins.whenHas<JavaPlugin> {
            plugins.apply<MindustryJavaPlugin>()
        }
        GroovyBridge.attach(target)
    }

    companion object {
        /**
         * The default check time(ms) for latest version.
         *
         * 1 hour as default.
         */
        const val defaultOutOfDataTime = 1000L * 60 * 60
        /**
         * The check time(ms) for latest version.
         *
         * 1 hour as default.
         */
        var outOfDataTime = defaultOutOfDataTime
        /**
         * A task group for main tasks, named `mindustry`
         */
        const val MindustryTaskGroup = "mindustry"
        /**
         * A task group for tasks related to [MindustryAssetsExtension], named `mindustry assets`
         */
        const val MindustryAssetTaskGroup = "mindustry assets"
        /**
         * The name of [MindustryExtension]
         */
        const val MainExtensionName = "mindustry"
        /**
         * The name of [MindustryAssetsExtension]
         */
        const val AssetExtensionName = "mindustryAssets"
        /**
         * The environment variable, as a folder, for Mindustry client to store data
         */
        const val MindustryDataDirEnv = "MINDUSTRY_DATA_DIR"
        /**
         * The default minGameVersion in `mod.(h)json`.
         *
         * **Note:** You shouldn't pretend this version and work based on it.
         */
        const val DefaultMinGameVersion = "135"
        /**
         * [The default Mindustry version](https://github.com/Anuken/Mindustry/releases/tag/v135)
         *
         * **Note:** You shouldn't pretend this version and work based on it.
         */
        const val DefaultMindustryVersion = "v135"
        /**
         * [The default bleeding edge version](https://github.com/Anuken/MindustryBuilds/releases/tag/22767)
         *
         * **Note:** You shouldn't pretend this version and work based on it.
         */
        const val DefaultMindustryBEVersion = "22826"
        /**
         * [The default Arc version](https://github.com/Anuken/Arc/releases/tag/v135.2)
         *
         * **Note:** You shouldn't pretend this version and work based on it.
         */
        const val DefaultArcVersion = "v135"
        /**
         * [Mindustry official release](https://github.com/Anuken/Mindustry/releases)
         */
        const val MindustryOfficialReleaseURL = "https://github.com/Anuken/Mindustry/releases"
        /**
         * GitHub API of [Mindustry official release](https://api.github.com/repos/Anuken/Mindustry/releases/latest)
         */
        const val APIMindustryOfficialReleaseURL = "https://api.github.com/repos/Anuken/Mindustry/releases"
        /**
         * GitHub API of [Latest Mindustry official release](https://api.github.com/repos/Anuken/Mindustry/releases/latest)
         */
        const val APIMindustryOfficialLatestReleaseURL = "https://api.github.com/repos/Anuken/Mindustry/releases/latest"
        /**
         * GitHub API of [Mindustry bleeding-edge release](https://api.github.com/repos/Anuken/Mindustry/releases/latest)
         */
        const val APIMindustryBEReleaseURL = "https://api.github.com/repos/Anuken/MindustryBuilds/releases/latest"
        /**
         * GitHub API of [Latest Mindustry bleeding-edge release](https://api.github.com/repos/Anuken/Mindustry/releases/latest)
         */
        const val APIMindustryBELatestReleaseURL = "https://api.github.com/repos/Anuken/MindustryBuilds/releases/latest"
        /**
         * [Arc tags](https://github.com/Anuken/Arc/tags)
         */
        const val ArcTagURL = "https://api.github.com/repos/Anuken/arc/tags"
        /**
         * [An *Anime* cat](https://github.com/Anuken)
         */
        const val Anuken = "anuken"
        /**
         * [Mindustry game](https://github.com/Anuken/Mindustry)
         */
        const val Mindustry = "mindustry"
        /**
         * [Mindustry bleeding-edge](https://github.com/Anuken/MindustryBuilds)
         */
        const val MindustryBuilds = "MindustryBuilds"
        /**
         * [The name convention of client release](https://github.com/Anuken/Mindustry/releases)
         */
        const val ClientReleaseName = "Mindustry.jar"
        /**
         * [The name convention of server release](https://github.com/Anuken/Mindustry/releases)
         */
        const val ServerReleaseName = "server-release.jar"
        /**
         * [The Mindustry repo on Jitpack](https://github.com/anuken/mindustry)
         */
        const val MindustryJitpackRepo = "com.github.anuken.mindustry"
        /**
         * [The mirror repo of Mindustry on Jitpack](https://github.com/anuken/mindustryjitpack)
         */
        const val MindustryJitpackMirrorRepo = "com.github.anuken.mindustryjitpack"
        /**
         * [The GitHub API to fetch the latest commit of mirror](https://github.com/Anuken/MindustryJitpack/commits/main)
         */
        const val MindustryJitpackLatestCommit = "https://api.github.com/repos/Anuken/MindustryJitpack/commits/main"
        /**
         * [The GitHub API to fetch the latest commit of arc](https://github.com/Anuken/Arc/commits/master)
         */
        const val ArcLatestCommit = "https://api.github.com/repos/Anuken/Arc/commits/master"
        /**
         * [The Arc repo on Jitpack](https://github.com/anuken/arc)
         */
        const val ArcJitpackRepo = "com.github.anuken.arc"
        /**
         * The main class of desktop launcher.
         */
        const val MindustryDesktopMainClass = "mindustry.desktop.DesktopLauncher"
        /**
         * The main class of server launcher.
         */
        const val MindustrySeverMainClass = "mindustry.server.ServerLauncher"
        /**
         * An empty folder for null-check
         */
        @JvmStatic
        val DefaultEmptyFile = File("")
        /**
         * The [organization](https://github.com/mindustry-antigrief) of Foo's Client
         */
        const val AntiGrief = "mindustry-antigrief"
        /**
         * The [Foo's Client repo](https://github.com/mindustry-antigrief/mindustry-client)
         */
        const val FooClient = "mindustry-client"
    }
}
/**
 * For downloading and running game.
 */
class MindustryAppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val ex = target.extensions.getOrCreate<MindustryExtension>(
            Mgpp.MainExtensionName
        )
        val resolveMods = target.tasks.register<ResolveMods>(
            "resolveMods"
        ) {
            group = Mgpp.MindustryTaskGroup
            mods.set(ex._mods.worksWith)
        }
        // For client side
        val downloadClient = target.tasks.register<Download>(
            "downloadClient",
        ) {
            group = Mgpp.MindustryTaskGroup
            keepOthers.set(ex._client.keepOtherVersion)
            location.set(ex._client.location)
        }
        // For server side
        val downloadServer = target.tasks.register<Download>(
            "downloadServer",
        ) {
            group = Mgpp.MindustryTaskGroup
            keepOthers.set(ex._client.keepOtherVersion)
            location.set(ex._server.location)
        }
        target.afterEvaluateThis {
            val runClient = tasks.register<RunMindustry>("runClient") {
                group = Mgpp.MindustryTaskGroup
                dependsOn(downloadClient)
                mainClass.convention(Mgpp.MindustryDesktopMainClass)
                val doForciblyClear = project.localProperties.getProperty("mgpp.run.forciblyClear")?.let {
                    it != "false"
                } ?: ex._run._forciblyClear.get()
                forciblyClear.set(doForciblyClear)
                val dataDirConfig = project.localProperties.getProperty("mgpp.run.dataDir") ?: ex._run._dataDir.get()
                val resolvedDataDir = if (dataDirConfig != "default" && dataDirConfig != "temp")
                    File(dataDirConfig)
                else if (dataDirConfig == "temp")
                    temporaryDir.resolve("data")
                else // Default data directory
                    resolveDefaultDataDir()
                logger.info("Data directory of $name is $resolvedDataDir .")
                dataDir.set(resolvedDataDir)
                mindustryFile.setFrom(downloadClient)
                modsWorkWith.setFrom(resolveMods)
                dataModsPath.set("mods")
                ex._mods._extraModsFromTask.get().forEach {
                    outputtedMods.from(tasks.getByPath(it))
                }
            }
            val runServer = tasks.register<RunMindustry>(
                "runServer",
            ) {
                group = Mgpp.MindustryTaskGroup
                dependsOn(downloadServer)
                val doForciblyClear = project.localProperties.getProperty("mgpp.run.forciblyClear")?.let {
                    it != "false"
                } ?: ex._run._forciblyClear.get()
                forciblyClear.set(doForciblyClear)
                mainClass.convention(Mgpp.MindustrySeverMainClass)
                mindustryFile.setFrom(downloadServer)
                modsWorkWith.setFrom(resolveMods)
                dataModsPath.convention("config/mods")
                ex._mods._extraModsFromTask.get().forEach {
                    dependsOn(tasks.getByPath(it))
                    outputtedMods.from(tasks.getByPath(it))
                }
            }
        }
    }
}
/**
 * It transports the Jar task output to running task.
 */
class MindustryJavaPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        val ex = extensions.getOrCreate<MindustryExtension>(
            Mgpp.MainExtensionName
        )
        @DisableIfWithout("java")
        val dexJar = tasks.register<DexJar>("dexJar") {
            dependsOn("jar")
            group = Mgpp.MindustryTaskGroup
            dependsOn(JavaPlugin.JAR_TASK_NAME)
            classpath.from(
                configurations.compileClasspath,
                configurations.runtimeClasspath
            )
            val jar = tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME)
            jarFiles.from(jar)
            sdkRoot.set(ex._deploy._androidSdkRoot)
        }
        @DisableIfWithout("java")
        tasks.register<Jar>("deploy") {
            group = Mgpp.MindustryTaskGroup
            val jar = tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME)
            dependsOn(jar)
            dependsOn(dexJar)
            destinationDirectory.set(temporaryDir)
            archiveBaseName.set(ex._deploy._baseName)
            archiveVersion.set(ex._deploy._version)
            archiveClassifier.set(ex._deploy._classifier)
            from(
                *jar.get().outputs.files.map { project.zipTree(it) }.toTypedArray(),
                *dexJar.get().outputs.files.map { project.zipTree(it) }.toTypedArray(),
            )
        }
        target.afterEvaluateThis {
            if (ex._deploy.enableFatJar.get()) {
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
        val modMeta = ex._modMeta.get()
        ex._deploy._baseName.convention(provider {
            modMeta.name
        })
        ex._deploy._version.convention(provider {
            modMeta.version
        })
    }
}
/**
 * Provides the existing [compileGroovy][org.gradle.api.tasks.compile.GroovyCompile] task.
 */
val TaskContainer.`dexJar`: TaskProvider<DexJar>
    get() = named<DexJar>("dexJar")
/**
 * Provides the existing [compileGroovy][org.gradle.api.tasks.compile.GroovyCompile] task.
 */
val TaskContainer.`deploy`: TaskProvider<Jar>
    get() = named<Jar>("deploy")

class MindustryAssetPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        val main = extensions.getOrCreate<MindustryExtension>(
            Mgpp.MainExtensionName
        )
        val assets = extensions.getOrCreate<MindustryAssetsExtension>(
            Mgpp.AssetExtensionName
        )
        // Register this for dynamically configure tasks without class reference in groovy.
        // Eagerly configure this task in order to be added into task group in IDE
        tasks.register<AntiAlias>("antiAlias") {
            group = Mgpp.MindustryTaskGroup
        }.get()
        val genModHjson = tasks.register<ModHjsonGenerate>("genModHjson") {
            group = Mgpp.MindustryTaskGroup
            modMeta.set(main._modMeta)
            outputHjson.set(temporaryDir.resolve("mod.hjson"))
        }
        plugins.whenHas<JavaPlugin> {
            tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
                dependsOn(genModHjson)
                from(genModHjson)
            }
        }
        // Doesn't register the tasks if no resource needs to generate its class.
        plugins.whenHas<JavaPlugin> {
            @DisableIfWithout("java")
            val genResourceClass by lazy {
                tasks.register<GenerateRClass>("genResourceClass") {
                    this.group = Mgpp.MindustryAssetTaskGroup
                    val name = assets.qualifiedName.get()
                    if (name == "default") {
                        val modMeta = main._modMeta.get()
                        val (packageName, _) = modMeta.main.packageAndClassName()
                        qualifiedName.set("$packageName.R")
                    } else {
                        qualifiedName.set(name)
                    }
                }
            }
            target.afterEvaluateThis {
                val assetsRoot = assets.assetsRoot.get()
                if (assetsRoot != Mgpp.DefaultEmptyFile) {
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
                            if (root == Mgpp.DefaultEmptyFile) {
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
                    @DisableIfWithout("java")
                    val gen = tasks.register<GenerateResourceClass>("gen${groupPascal}Class") {
                        this.group = Mgpp.MindustryAssetTaskGroup
                        dependsOn(batches.flatMap { it.dependsOn }.distinct().toTypedArray())
                        args.put("ModName", main._modMeta.get().name)
                        args.put("ResourceNameRule", type.nameRule.name)
                        args.putAll(assets.args)
                        args.putAll(type.args)
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
}
/**
 * Provides the existing [antiAlias][AntiAlias] task.
 */
val TaskContainer.`antiAlias`: TaskProvider<AntiAlias>
    get() = named<AntiAlias>("antiAlias")
/**
 * Provides the existing [genModHjson][ModHjsonGenerate] task.
 */
val TaskContainer.`genModHjson`: TaskProvider<ModHjsonGenerate>
    get() = named<ModHjsonGenerate>("genModHjson")

inline fun safeRun(func: () -> Unit) {
    try {
        func()
    } catch (_: Throwable) {
    }
}
/**
 * Provides the existing [resolveMods][ResolveMods] task.
 */
val TaskContainer.`resolveMods`: TaskProvider<ResolveMods>
    get() = named<ResolveMods>("resolveMods")

fun Project.resolveDefaultDataDir(): File {
    return when (getOs()) {
        OS.Unknown -> {
            logger.warn("Can't recognize your operation system.")
            Mgpp.DefaultEmptyFile
        }
        OS.Windows -> FileAt(System.getenv("AppData"), "Mindustry")
        OS.Linux -> FileAt(System.getenv("XDG_DATA_HOME") ?: System.getenv("HOME"), ".local", "share", "Mindustry")
        OS.Mac -> FileAt(System.getenv("HOME"), "Library", "Application Support", "Mindustry")
    }
}