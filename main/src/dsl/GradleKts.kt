@file:JvmMultifileClass
@file:JvmName("DslKt")
@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.dsl

import org.gradle.api.*
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.provider.*
import org.gradle.api.tasks.*
import java.io.File
import kotlin.reflect.KClass

internal
fun Task.deleteTempDir() {
    project.delete(temporaryDir.listFiles())
}
internal
fun Task.tempFi(name: String) =
    temporaryDir.resolve(name)
internal
fun Project.projDir(name: String) =
    projectDir.resolve(name)
internal
fun Project.rootDir(name: String) =
    rootDir.resolve(name)
internal inline
fun <reified T> ExtensionContainer.getOrCreate(
    extensionName: String,
): T {
    return findByType(T::class.java) ?: create(extensionName, T::class.java)
}
internal
fun Project.stringProp(): StringProp =
    objects.property(String::class.java)
internal
fun Project.boolProp(): BoolProp =
    objects.property(Boolean::class.java)
internal
fun Project.stringsProp(): StringsProp =
    objects.listProperty(String::class.java)
internal inline
fun <reified T> Project.listProp(): ListProperty<T> =
    objects.listProperty(T::class.java)
internal inline
fun <reified TK, reified TV> Project.mapProp(): MapProperty<TK, TV> =
    project.objects.mapProperty(TK::class.java, TV::class.java)
internal inline
fun <reified T> Project.setProp(): SetProperty<T> =
    objects.setProperty(T::class.java)
internal inline
fun <reified T> Project.prop(): Property<T> =
    objects.property(T::class.java)
internal
fun Project.dirProp(): DirProp =
    objects.directoryProperty()
internal
fun Project.sourceDirectorySet(name: String, displayName: String): SourceDirectorySet =
    objects.sourceDirectorySet(name, displayName)
internal
fun Project.fileProp(): FileProp =
    objects.property(File::class.java)
internal
fun Project.configurationFileCollection(): ConfigurableFileCollection =
    objects.fileCollection()
internal inline
fun <reified T> T.func(func: T.() -> Unit) {
    this.func()
}
internal inline
fun <reified T : Plugin<*>> PluginContainer.apply(
): T = this.apply(T::class.java)
internal inline
fun <reified T : Plugin<*>> PluginContainer.hasPlugin() =
    hasPlugin(T::class.java)
internal
fun Project.dirProv(file: File): Provider<Directory> {
    return layout.dir(provider { file })
}
internal inline
fun Project.dirProv(crossinline prov: () -> File): Provider<Directory> {
    return layout.dir(provider { prov() })
}

internal
inline fun <reified T> Project.new(): T =
    objects.newInstance(T::class.java)

internal
inline fun <reified T> DefaultTask.new(): T =
    project.new()
internal
fun Project.afterEvaluateThis(func: Project.() -> Unit) {
    afterEvaluate(func)
}

internal inline
fun <reified T : DefaultTask> TaskContainer.withType(
    noinline config: T.() -> Unit,
): DomainObjectCollection<T> = this.withType(T::class.java, config)
internal inline
fun <reified T : DefaultTask> TaskContainer.named(
    name: String,
    noinline config: T.() -> Unit,
): TaskProvider<T> = this.named(name, T::class.java, config)
@Suppress("unchecked_cast")
internal inline
fun <reified T : Task> TaskCollection<out Task>.named(
    name: String, noinline configuration: T.() -> Unit,
): TaskProvider<T> =
    (this as TaskCollection<T>).named(name, T::class.java, configuration)
/**
 * Locates a task by name and type, without triggering its creation or configuration, failing if there is no such task.
 *
 * @see [TaskCollection.named]
 */
@Suppress("unchecked_cast")
internal
fun <T : Task> TaskCollection<out Task>.named(name: String, type: KClass<T>): TaskProvider<T> =
    (this as TaskCollection<T>).named(name, type.java)
/**
 * Locates a task by name and type, without triggering its creation or configuration, failing if there is no such task.
 *
 * @see [TaskCollection.named]
 */
@Suppress("extension_shadowed_by_member")
internal
inline fun <reified T : Task> TaskCollection<out Task>.named(name: String): TaskProvider<T> =
    named(name, T::class)
/**
 * Defines a new task, which will be created when it is required.
 *
 * @see [TaskContainer.register]
 */
@Suppress("extension_shadowed_by_member")
internal
inline fun <reified T : Task> TaskContainer.register(name: String): TaskProvider<T> =
    register(name, T::class.java)
/**
 * Defines and configure a new task, which will be created when it is required.
 *
 * @see [TaskContainer.register]
 */
internal
inline fun <reified T : Task> TaskContainer.register(
    name: String,
    noinline configuration: T.() -> Unit
): TaskProvider<T> =
    register(name, T::class.java, configuration)
/**
 * Provides the existing [compileClasspath][org.gradle.api.artifacts.Configuration] element.
 */
internal
val NamedDomainObjectContainer<Configuration>.`compileClasspath`: NamedDomainObjectProvider<Configuration>
    get() = named("compileClasspath")
/**
 * Provides the existing [runtimeClasspath][org.gradle.api.artifacts.Configuration] element.
 */
internal
val NamedDomainObjectContainer<Configuration>.`runtimeClasspath`: NamedDomainObjectProvider<Configuration>
    get() = named("runtimeClasspath")
/**
 * Retrieves the [sourceSets][org.gradle.api.tasks.SourceSetContainer] extension.
 */
internal
val Project.`sourceSets`: SourceSetContainer
    get() =
        (this as org.gradle.api.plugins.ExtensionAware).extensions.getByName("sourceSets") as SourceSetContainer
/**
 * Configures the [sourceSets][org.gradle.api.tasks.SourceSetContainer] extension.
 */
internal
fun Project.`sourceSets`(configure: Action<SourceSetContainer>): Unit =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("sourceSets", configure)
/**
 * Provides the existing [main][org.gradle.api.tasks.SourceSet] element.
 */
internal
val SourceSetContainer.`main`: NamedDomainObjectProvider<SourceSet>
    get() = named("main")

