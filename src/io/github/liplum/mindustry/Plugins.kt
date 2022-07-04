@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.*
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
        try {
            plugins.apply<JavaPlugin>()
        } catch (e: Exception) {
            logger.warn("Your project doesn't support java plugin, so mgpp was disabled.", e)
            return@func
        }
        plugins.apply<MindustryAppPlugin>()
        plugins.apply<MindustryAssetPlugin>()
        plugins.apply<MindustryJavaPlugin>()
        GroovyBridge.attach(target)
    }

    companion object {
        const val MindustryTaskGroup = "mindustry"
        const val MindustryAssetTaskGroup = "mindustry assets"
        const val MainExtensionName = "mindustry"
        const val AssetExtensionName = "mindustryAssets"
        const val MindustryDataDirEnv = "MINDUSTRY_DATA_DIR"
        const val DefaultMinGameVersion = "135"
        const val DefaultMindustryVersion = "v135"
        const val DefaultMindustryBEVersion = "22767"
        const val DefaultArcVersion = "v135"
        const val OfficialReleaseURL = "https://api.github.com/repos/Anuken/Mindustry/releases/latest"
        const val BEReleaseURL = "https://api.github.com/repos/Anuken/MindustryBuilds/releases/latest"
        const val Anuken = "anuken"
        const val Mindustry = "mindustry"
        const val MindustryBuilds = "MindustryBuilds"
        const val ClientReleaseName = "Mindustry.jar"
        const val ServerReleaseName = "server-release.jar"
        const val MindustryJitpackRepo = "com.github.anuken.mindustry"
        const val MindustryJitpackMirrorRepo = "com.github.anuken.mindustryjitpack"
        const val MindustryJitpackLatestCommit = "https://api.github.com/repos/Anuken/MindustryJitpack/commits/main"
        const val ArcLatestCommit = "https://api.github.com/repos/Anuken/Arc/commits/master"
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
            Mgpp.MainExtensionName
        )
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
            sdkRoot.set(ex.deploy._androidSdkRoot)
        }
        tasks.register<Jar>("deploy") {
            group = Mgpp.MindustryTaskGroup
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
        val modMeta = ex.modMeta.get()
        ex.deploy._baseName.convention(provider {
            modMeta.name
        })
        ex.deploy._version.convention(provider {
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
        val genModHjson = tasks.register<ModHjsonGenerate>("genModHjson") {
            group = Mgpp.MindustryTaskGroup
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
            group = Mgpp.MindustryTaskGroup
        }.get()
        // Doesn't register the tasks if no resource needs to generate its class.
        val genResourceClass by lazy {
            tasks.register<RClassGenerate>("genResourceClass") {
                this.group = Mgpp.MindustryAssetTaskGroup
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
                val gen = tasks.register<ResourceClassGenerate>("gen${groupPascal}Class") {
                    this.group = Mgpp.MindustryAssetTaskGroup
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
/**
 * Provides the existing [compileGroovy][org.gradle.api.tasks.compile.GroovyCompile] task.
 */
val TaskContainer.`antiAlias`: TaskProvider<AntiAlias>
    get() = named<AntiAlias>("antiAlias")
/**
 * Provides the existing [compileGroovy][org.gradle.api.tasks.compile.GroovyCompile] task.
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
            mods.set(ex.mods.worksWith)
        }
        target.afterEvaluateThis {
            // For client side
            val downloadClient = tasks.register<Download>(
                "downloadClient",
            ) {
                group = Mgpp.MindustryTaskGroup
                keepOthers.set(ex.client.keepOtherVersion)
                ex.client.location.get().run {
                    val downloadLocation = GitHubDownload.release(
                        user, repo,
                        version, release
                    )
                    location.set(downloadLocation)
                    outputFileName.set(
                        "${downloadLocation.name.removeSuffix(".jar")}-${user}-${repo}-${version}.jar"
                    )
                }
            }
            // For server side
            val downloadServer = tasks.register<Download>(
                "downloadServer",
            ) {
                group = Mgpp.MindustryTaskGroup
                keepOthers.set(ex.client.keepOtherVersion)
                ex.server.location.get().run {
                    val downloadLocation = GitHubDownload.release(
                        user, repo,
                        version, release
                    )
                    location.set(downloadLocation)
                    outputFileName.set(
                        "${downloadLocation.name.removeSuffix(".jar")}-${user}-${repo}-${version}.jar"
                    )
                }
            }
            val dataDirEx = ex.run._dataDir.get()
            val runClient = tasks.register<RunMindustry>("runClient") {
                group = Mgpp.MindustryTaskGroup
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
                    outputtedMods.from(tasks.getByPath(it))
                }
            }
            val runServer = tasks.register<RunMindustry>(
                "runServer",
            ) {
                group = Mgpp.MindustryTaskGroup
                dependsOn(downloadServer)
                mainClass.convention(Mgpp.MindustrySeverMainClass)
                mindustryFile.setFrom(downloadServer)
                modsWorkWith.setFrom(resolveMods)
                dataModsPath.convention("config/mods")
                ex.mods._extraModsFromTask.get().forEach {
                    dependsOn(tasks.getByPath(it))
                    outputtedMods.from(tasks.getByPath(it))
                }
            }
        }
    }
}
/**
 * Provides the existing [compileGroovy][org.gradle.api.tasks.compile.GroovyCompile] task.
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