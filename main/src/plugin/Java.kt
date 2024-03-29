@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar

/**
 * For deployment.
 */
class MindustryJavaPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        if (!plugins.hasPlugin<JavaPlugin>()) {
            throw GradleException("${MindustryJavaPlugin::class.java} requires `java` plugin applied.")
        }
        val deployX = extensions.getOrCreate<DeployModExtension>(R.x.deployMod)
        val assets = extensions.getOrCreate<MindustryAssetsExtension>(R.x.mindustryAssets)
        val dexJar = tasks.register<DexJar>(R.task.dexJar) {
            dependsOn(JavaPlugin.JAR_TASK_NAME)
            group = R.taskGroup.mindustry
            dependsOn(JavaPlugin.JAR_TASK_NAME)
            classpath.from(
                configurations.compileClasspath,
                configurations.runtimeClasspath
            )
            jarFiles.from(tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME))
        }
        parent?.let {
            // disable those if current project is subproject.
            deployX.fatJar = false
            deployX.outputMod = false
        }
        val ex = extensions.getOrCreate<MindustryExtension>(R.x.mindustry)
        // Set the convention to ex._deploy
        deployX._baseName.convention(provider {
            ex._modMeta.get().name
        })
        deployX._version.convention(provider {
            ex._modMeta.get().version
        })
        val deploy = tasks.register<Jar>(R.task.deployMod) {
            group = R.taskGroup.mindustry
            dependsOn(tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME))
            dependsOn(dexJar)
            destinationDirectory.set(layout.buildDirectory.asFile.get().resolve(R.task.deployMod))
            archiveBaseName.set(deployX._baseName)
            archiveVersion.set(deployX._version)
            archiveClassifier.set(deployX._classifier)

        }
        target.afterEvaluateThis {
            if (ex._modMeta.isPresent) {
                deploy.configure {
                    it.from(
                        *tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME).get().outputs.files.map {
                            project.zipTree(it)
                        }.toTypedArray()
                    )
                    it.from(*dexJar.get().outputs.files.map { project.zipTree(it) }.toTypedArray())
                }
                tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
                    if (deployX.fatJar) {
                        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                        from(
                            configurations.runtimeClasspath.get().map {
                                if (it.isDirectory) it else zipTree(it)
                            }
                        )
                    }
                }
                tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
                    if (deployX.outputMod) {
                        from(assets.assets)
                        from(assets._icon)
                        from(tasks.findByPath(R.task.genModHjson))
                    }
                }
            }
        }
    }
}

/**
 * Provides the existing [dexJar][DexJar] task.
 */
val TaskContainer.`dexJar`: TaskProvider<DexJar>
    get() = named<DexJar>(R.task.dexJar)

/**
 * Provides the existing [deployMod][Jar] task.
 */
val TaskContainer.`deploy`: TaskProvider<Jar>
    get() = named<Jar>(R.task.deployMod)