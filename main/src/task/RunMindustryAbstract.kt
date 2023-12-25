package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.dsl.fileProp
import io.github.liplum.dsl.stringsProp
import org.gradle.api.tasks.*
import java.io.File

abstract class RunMindustryAbstract : JavaExec() {
    val gameFile = project.fileProp()
        @InputFile get
    val dataDir = project.prop<IDataDirLoc>()
        @Optional @Input get
    val startupArgs = project.stringsProp()
        @Input @Optional get
    val mods = project.configurationFileCollection()
        @InputFiles @Optional get

    init {
        mainClass.set("-jar")
    }

    fun loadMods(modsDir: File) {
        modsDir.mkdirs()
        if (modsDir.isDirectory) {
            // TODO: Record the mod signature, and don't always delete all.
            modsDir.deleteRecursively()
        }
        for (modFile in mods) {
            if (modFile.isFile) {
                createSymbolicLinkOrCopy(link = modsDir.resolve(modFile.name), target = modFile)
            } else {
                logger.error("Mod<$modFile> doesn't exist.")
            }
        }
    }
}