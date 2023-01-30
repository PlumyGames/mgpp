package io.github.liplum.mindustry

import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import java.io.File

class Modpack(
    val name: String
) {
    val mods = ArrayList<IMod>()
}

class AddModpackSpec(
    private val proj: Project,
    private val modpack: Modpack
) {
    fun addMod(mod: IMod) {
        modpack.mods.add(mod)
    }
    /**
     * Add a mod by its repo name on GitHub, and detect its mode type automatically.
     *
     * **Not recommended:** Please use any more specific method, such as [java], [json] or [js]
     * @param repo like "PlumyGames/mgpp"
     */
    fun github(repo: String) = addMod(GitHubMod(repo))
    /**
     * Add a json mod by its repo name on GitHub.
     * @param repo like "PlumyGames/mgpp"
     */
    fun json(repo: String) = addMod(GitHubPlainMod(repo))
    /**
     * Add a javascript mod by its repo name on GitHub.
     * @param repo like "PlumyGames/mgpp"
     */
    fun js(repo: String) = addMod(GitHubPlainMod(repo))
    /**
     * Add a java mod by its repo name on GitHub.
     * @param repo like "PlumyGames/mgpp"
     */
    fun java(repo: String) = addMod(GitHubJvmMod(repo))
    /**
     * Add a jvm mod by its repo name on GitHub.
     * @param repo like "PlumyGames/mgpp"
     */
    fun jvm(repo: String) = addMod(GitHubJvmMod(repo))
    /**
     * Add a local mod form disk.
     *
     * **Suggestion** To use a relative path and embed the mod into project directory
     * would be better for git or collaboration.
     * @param path of that mod
     */
    fun local(path: String) = addMod(LocalMod(path))
    /**
     * Add a local mod form disk.
     *
     * **Suggestion** To use a relative path and embed the mod into project directory
     * would be better for git or collaboration.
     * @param file of that mod
     */
    fun local(file: File) = addMod(LocalMod(file))
    /** @see [github]  */
    fun github(props: Map<String, String>) = github(props["repo"] ?: "")
    /** @see [json]  */
    fun json(props: Map<String, String>) = json(props["repo"] ?: "")
    /** @see [js]  */
    fun js(props: Map<String, String>) = js(props["repo"] ?: "")
    /** @see [js]  */
    fun jvm(props: Map<String, String>) = jvm(props["repo"] ?: "")

    /** @see [local]  */
    fun local(props: Map<String, Any>) {
        val path = props["path"]
        val file = props["file"]
        if (path != null) {
            local(path as String)
        } else if (file != null) {
            local(file as File)
        } else {
            proj.logger.log(
                LogLevel.WARN,
                "Neither \"path\" nor \"file\" given in local(Map<String,Any>)"
            )
        }
    }

}