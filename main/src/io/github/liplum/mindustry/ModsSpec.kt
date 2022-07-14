package io.github.liplum.mindustry

import io.github.liplum.dsl.listProp
import io.github.liplum.dsl.stringsProp
import io.github.liplum.dsl.whenHas
import io.github.liplum.mindustry.LocalProperties.localProperties
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

/**
 * You can add more mods working with your mod, such as a Json mod or Java mod from GitHub,
 * a local file, an url or even a gradle task.
 */
class ModsSpec(
    val target: Project,
) {
    val _extraModsFromTask = target.stringsProp().apply {
        target.plugins.whenHas<JavaPlugin> {
            add(JavaPlugin.JAR_TASK_NAME)
        }
    }
    /**
     * The mods form outputs of another task.
     *
     * It includes `:jar` task as default
     */
    var extraModsFromTask: List<String>
        get() = _extraModsFromTask.getOrElse(emptyList())
        set(value) {
            _extraModsFromTask.set(value)
        }
    /**
     * What mods your mod works with
     */
    val worksWith = target.listProp<IMod>().apply {
        convention(HashSet())
    }
    /**
     * such as :
     *
     * ```add java "PlumyGame/mgpp"```
     */
    // For Kotlin
    val add: ModsSpec
        get() = this
    /**
     * Configure what mods your mod works with
     */
    fun worksWith(config: Runnable) {
        config.run()
    }
    /**
     * Configure what mods your mod works with
     */
    inline fun worksWith(config: () -> Unit) {
        config()
    }
    /**
     * Add some mods working with your mod.
     */
    fun worksWith(vararg mods: IMod) {
        worksWith.addAll(mods.toList())
    }
    /**
     * Add a mod form GitHub by its repo name.
     * **Not recommended:** Please use any more specific method, such as [java] or [json]
     * @param repo like "PlumyGame/mgpp"
     */
    infix fun github(repo: String)=GitHubMod(repo).apply {
        worksWith.add(this)
    }
    /**
     * Add a json mod form GitHub by its repo name.
     * @param repo like "PlumyGame/mgpp"
     */
    infix fun json(repo: String) = GitHubPlainMod(repo).apply {
        worksWith.add(this)
    }
    /**
     * Add a json mod form GitHub by its repo name.
     * @param repo like "PlumyGame/mgpp"
     */
    infix fun hjson(repo: String) = GitHubPlainMod(repo).apply {
        worksWith.add(this)
    }
    /**
     * Add a js mod form GitHub by its repo name.
     * @param repo like "PlumyGame/mgpp"
     */
    infix fun js(repo: String) = GitHubPlainMod(repo).apply {
        worksWith.add(this)
    }
    /**
     * Add a java mod form GitHub by its repo name.
     * @param repo like "PlumyGame/mgpp"
     */
    infix fun java(repo: String) = GitHubJvmMod(repo).apply {
        worksWith.add(this)
    }
    /**
     * Add a kotlin mod form GitHub by its repo name.
     * @param repo like "PlumyGame/mgpp"
     */
    infix fun kotlin(repo: String) = GitHubJvmMod(repo).apply {
        worksWith.add(this)
    }
    /**
     * Add a groovy mod form GitHub by its repo name.
     * @param repo like "PlumyGame/mgpp"
     */
    infix fun groovy(repo: String) = GitHubJvmMod(repo).apply {
        worksWith.add(this)
    }
    /**
     * Add a scala mod form GitHub by its repo name.
     * @param repo like "PlumyGame/mgpp"
     */
    infix fun scala(repo: String) = GitHubJvmMod(repo).apply {
        worksWith.add(this)
    }
    /**
     * Add a closure mod form GitHub by its repo name.
     * @param repo like "PlumyGame/mgpp"
     */
    infix fun closure(repo: String) = GitHubJvmMod(repo).apply {
        worksWith.add(this)
    }
    /**
     * Add a local mod form disk.
     *
     * **Suggestion** To use a relative path and embed the mod into project directory
     * would be better for git or collaboration.
     * @param path of that mod
     */
    infix fun local(path: String) = LocalMod(path).apply {
        worksWith.add(this)
    }
    /**
     * Add a local mod form disk by the [key] in local.properties.
     * @param key in local.properties
     */
    infix fun localProperties(key: String) {
        val path = target.localProperties.getProperty(key)
        if (path != null) {
            worksWith.add(LocalMod(path))
        } else {
            target.logger.warn("$key not found in local.properties.")
        }
    }
    /**
     * Add a mod from [url]
     * @param url any resource
     */
    infix fun url(url: String) = UrlMod(url).apply {
        worksWith.add(this)
    }
    /**
     * Add a mod from the outputs of another task.
     * @param task its name or path, such as `:zip`, `:jar` or `:main:zip`
     */
    infix fun fromTask(task: String) {
        _extraModsFromTask.add(task)
    }
}