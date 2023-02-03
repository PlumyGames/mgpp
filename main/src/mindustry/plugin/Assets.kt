@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.mindustry.*
import io.github.liplum.mindustry.task.GenerateRClass
import io.github.liplum.mindustry.task.GenerateResourceClass
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.jvm.tasks.Jar


@DisableIfWithout("java")
class MindustryAssetPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        val main = extensions.getOrCreate<MindustryExtension>(
            R.x.mindustry
        )
        val assets = extensions.getOrCreate<MindustryAssetsExtension>(
            R.x.mindustryAssets
        )
        // Doesn't register the tasks if no resource needs to generate its class.
        @DisableIfWithout("java")
        val genResourceClass by lazy {
            tasks.register<GenerateRClass>("genResourceClass") {
                this.group = R.taskGroup.mindustryAsset
                val name = assets.qualifiedName.get()
                if (name == "default") {
                    val modMeta = main._modMeta.get()
                    // TODO: If main is empty
                    val (packageName, _) = (modMeta.main ?: "").packageAndClassName()
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
            if (!main.isLib) {
                tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
                    from(assets._icon)
                }
                tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
                    dependsOn("genModHjson")
                    from(tasks.getByPath("genModHjson"))
                }
            }
            // Resolve all batches
            val group2Batches = assets.batches.get().resolveBatches()
            var genResourceClassCounter = 0
            val jar = tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME)
            for ((type, batches) in group2Batches) {
                if (batches.isEmpty()) continue
                jar.configure {
                    batches.forEach { batch ->
                        val dir = batch.dir
                        val root = batch.root
                        if (root == MindustryPlugin.DefaultEmptyFile) {
                            val dirParent = dir.parentFile
                            if (dirParent != null) {
                                it.from(dirParent) {
                                    it.include("${dir.name}/**")
                                }
                            } else {
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
                    this.group = R.taskGroup.mindustryAsset
                    dependsOn(batches.flatMap { it.dependsOn }.distinct().toTypedArray())
                    args.put("ModName", main._modMeta.get().name)
                    args.put("ResourceNameRule", type.nameRule.name)
                    args.putAll(assets.args)
                    args.putAll(type.args)
                    generator.set(type.generator)
                    className.set(type.className)
                    resources.from(batches.filter { it.enableGenClass }.map { it.dir })
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