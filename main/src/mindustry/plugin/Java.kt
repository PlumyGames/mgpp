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
        val jar = tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME)
        @DisableIfWithout("java")
        val dexJar = tasks.register<DexJar>("dexJar") {
            dependsOn("jar")
            group = R.taskGroup.mindustry
            dependsOn(JavaPlugin.JAR_TASK_NAME)
            classpath.from(
                configurations.compileClasspath,
                configurations.runtimeClasspath
            )
            jarFiles.from(jar)
            sdkRoot.set(ex._deploy._androidSdkRoot)
        }
        val deploy = tasks.register<Jar>("deploy") {
            group = R.taskGroup.mindustry
            dependsOn(jar)
            dependsOn(dexJar)
            destinationDirectory.set(temporaryDir)
            archiveBaseName.set(ex._deploy._baseName)
            archiveVersion.set(ex._deploy._version)
            archiveClassifier.set(ex._deploy._classifier)
        }
        target.afterEvaluateThis {
            deploy.configure { deploy ->
                deploy.from(
                    *jar.get().outputs.files.map { project.zipTree(it) }.toTypedArray(),
                    *dexJar.get().outputs.files.map { project.zipTree(it) }.toTypedArray(),
                )
            }
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
        // Set the convention to ex._deploy
        ex._deploy._baseName.convention(provider {
            ex._modMeta.get().name
        })
        ex._deploy._version.convention(provider {
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