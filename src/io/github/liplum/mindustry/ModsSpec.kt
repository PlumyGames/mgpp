package io.github.liplum.mindustry

import io.github.liplum.dsl.listProp
import io.github.liplum.dsl.stringsProp
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

/**
 * You can add more mods working with your mod, such as a Json mod or Java mod from GitHub,
 * a local file, an url or even a gradle task.
 */
class ModsSpec(
    target: Project,
) {
    val _extraModsFromTask = target.stringsProp().apply {
        add(JavaPlugin.JAR_TASK_NAME)
    }
    var extraModsFromTask: List<String>
        get() = _extraModsFromTask.getOrElse(emptyList())
        set(value) {
            _extraModsFromTask.set(value)
        }
    val worksWith = target.listProp<IMod>().apply {
        convention(HashSet())
    }
    val add: ModsSpec
        get() = this

    fun worksWith(config: Runnable) {
        config.run()
    }

    inline fun worksWith(config: () -> Unit) {
        config()
    }

    infix fun github(repo: String) {
        worksWith.add(GitHubMod(repo))
    }

    infix fun json(repo: String) = GitHubPlainMod(repo).apply {
        worksWith.add(this)
    }

    infix fun hjson(repo: String) = GitHubPlainMod(repo).apply {
        worksWith.add(this)
    }

    infix fun js(repo: String) = GitHubPlainMod(repo).apply {
        worksWith.add(this)
    }

    infix fun java(repo: String) {
        worksWith.add(GitHubJvmMod(repo))
    }

    infix fun kotlin(repo: String) {
        worksWith.add(GitHubJvmMod(repo))
    }

    infix fun groovy(repo: String) {
        worksWith.add(GitHubJvmMod(repo))
    }

    infix fun scala(repo: String) {
        worksWith.add(GitHubJvmMod(repo))
    }

    infix fun closure(repo: String) {
        worksWith.add(GitHubJvmMod(repo))
    }

    infix fun local(path: String) {
        worksWith.add(LocalMod(path))
    }

    infix fun url(url: String) {
        worksWith.add(UrlMod(url))
    }

    infix fun fromTask(task: String) {
        _extraModsFromTask.add(task)
    }
    /**
     * Add some mods working with this mod.
     */
    fun worksWith(vararg mods: IMod) {
        worksWith.addAll(mods.toList())
    }
}