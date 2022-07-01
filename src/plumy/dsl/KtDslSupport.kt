@file:Suppress("RemoveRedundantBackticks")

package plumy.dsl

import org.gradle.api.*
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.*
import kotlin.reflect.KClass

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
inline fun <reified T : Task> TaskContainer.register(name: String, noinline configuration: T.() -> Unit): TaskProvider<T> =
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

