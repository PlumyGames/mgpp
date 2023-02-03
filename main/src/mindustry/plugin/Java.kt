@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.mindustry.*
import io.github.liplum.mindustry.task.DexJar
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
@DisableIfWithout("java")
class MindustryJavaPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.func {
        val ex = extensions.getOrCreate<MindustryExtension>(
            R.x.mindustry
        )
        val deployX = extensions.getOrCreate<DeployModExtension>(
            R.x.deployMod
        )
        target.parent?.let {
            deployX.enableFatJar = false
        }
        @DisableIfWithout("java")
        val dexJar = tasks.register<DexJar>("dexJar") {
            dependsOn("jar")
            group = R.taskGroup.mindustry
            dependsOn(JavaPlugin.JAR_TASK_NAME)
            classpath.from(
                configurations.compileClasspath,
                configurations.runtimeClasspath
            )
            jarFiles.from(tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME))
        }
        val deploy = tasks.register<Jar>("deploy") {
            group = R.taskGroup.mindustry
            dependsOn(tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME))
            dependsOn(dexJar)
            destinationDirectory.set(temporaryDir)
            archiveBaseName.set(deployX._baseName)
            archiveVersion.set(deployX._version)
            archiveClassifier.set(deployX._classifier)
        }
        target.afterEvaluateThis {
            deploy.configure { deploy ->
                deploy.from(
                    *tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME).get().outputs.files.map { project.zipTree(it) }
                        .toTypedArray(),
                    *dexJar.get().outputs.files.map { project.zipTree(it) }.toTypedArray(),
                )
            }
            if (deployX.enableFatJar) {
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
        // Set the convention to ex._deploy
        deployX._baseName.convention(provider {
            ex._modMeta.get().name
        })
        deployX._version.convention(provider {
            ex._modMeta.get().version
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
/**
 * For generating resource class.
 */