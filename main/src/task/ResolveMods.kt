package io.github.liplum.mindustry

import io.github.liplum.dsl.listProp
import io.github.liplum.mindustry.*
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import java.io.File
import kotlin.math.log

open class ResolveMods : DefaultTask() {
    val mods = project.listProp<IMod>()
        @Input get
    val downloadedMods: List<File>
        @OutputFiles get() = mods.get().map(::getModFileOf)

    fun getModFileOf(mod: IMod): File {
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
            if (mod is IGitHubMod) {
                val modFile = getModFileOf(mod)
                if (modFile.exists()) {
                    if (!mod.isUpdateToDate(modFile, logger = logger)) {
                        val temp = File.createTempFile(mod.fileName, null)
                        try {
                            mod.updateFile(temp, logger = logger)
                        } catch (e: Exception) {
                            logger.warn("Failed to update $mod", e)
                            continue
                        }
                        temp.copyTo(modFile)
                    }
                } else {
                    // download the mod
                    try {
                        mod.resolveFile(writeIn = modFile, logger = logger)
                        logger.info("resolved $mod at ${modFile.absolutePath}.")
                    } catch (e: Exception) {
                        // now mod is corrupted, delete it.
                        modFile.delete()
                        logger.warn("Failed to resolve $mod", e)
                    }
                }
            }
            if (mod is IDownloadableMod) {
                val modFile = getModFileOf(mod)
                try {
                    mod.resolveFile(writeIn = modFile, logger = logger)
                    logger.info("resolved $mod at ${modFile.absolutePath}.")
                } catch (e: Exception) {
                    // now mod is corrupted, delete it.
                    modFile.delete()
                    logger.warn("Failed to resolve $mod", e)
                }
            }
        }
    }
}
