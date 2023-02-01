package io.github.liplum.mindustry.task

import io.github.liplum.dsl.listProp
import io.github.liplum.mindustry.*
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ResolveMods : DefaultTask() {
    val mods = project.listProp<IMod>()
        @Input get
    val downloadedMods: List<File>
        @OutputFiles get() = mods.get().map(::getModFile)

    fun getModFile(mod: IMod): File {
        return when (mod) {
            is UrlMod -> SharedCache.modsDir.resolve("url").resolve(mod.fileName)
            is IDownloadableMod -> SharedCache.modsDir.resolve("github").resolve(mod.fileName)
            is LocalMod -> mod.modFile
            else -> throw GradleException("Unsupported mod type: $mod")
        }
    }
    @TaskAction
    fun resolve() {
        for (mod in mods.get()) {
            if (mod is LocalMod) continue
            if (mod is IDownloadableMod) {
                val modFile = getModFile(mod)
                try {
                    mod.resolveFile(writeIn = modFile)
                    logger.info("resolved $mod into ${modFile.absolutePath} .")
                } catch (e: Exception) {
                    logger.warn("Can't resolve $mod", e)
                }
            }
        }
    }
}