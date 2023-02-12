package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.mindustry.R
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.tasks.*
import java.io.File

open class RunClient : RunMindustryAbstract() {
    init {

    }
    @TaskAction
    override fun exec() {
        val originDataDir = dataDir.get().resolveDir(this)
        val dataDir = if (originDataDir != null) {
            environment[R.env.mindustryDataDir] = originDataDir.absoluteFile
            originDataDir
        } else {
            val default = resolveDefaultDataDir()
            if (default == null) {
                logger.warn("Failed to recognize your operation system and find corresponding Mindustry data directory.")
                val temp = temporaryDir.resolve(name)
                environment[R.env.mindustryDataDir] = temp.absoluteFile
                temp
            } else {
                default
            }
        }
        workingDir = dataDir
        if (dataDir.isDirectory) {
            // TODO: Record the mod signature.
            // TODO: Don't always delete all.
            dataDir.deleteRecursively()
        }
        dataDir.mkdirs()
        val modsFolder = dataDir.resolve("mods")
        for (modFile in mods) {
            if (modFile.isFile) {
                modFile.copyTo(modsFolder.resolve(modFile.name), overwrite = true)
            } else {
                logger.warn("Mod<$modFile> doesn't exist.")
            }
        }
        standardInput = System.`in`
        args = listOf(mindustryFile.get().absolutePath) + startupArgs.get()
        if (Os.isFamily(Os.FAMILY_MAC)) {
            // Lwjgl3 application requires it to run on macOS
            jvmArgs = (jvmArgs ?: mutableListOf()) + "-XstartOnFirstThread"
        }
        logger.lifecycle("Run client in $dataDir.")
        // run Mindustry
        super.exec()
    }

    internal
    fun resolveDefaultDataDir(): File? {
        return when (getOs()) {
            OS.Unknown -> null
            OS.Windows -> FileAt(System.getenv("AppData"), "Mindustry")
            OS.Linux -> FileAt(System.getenv("XDG_DATA_HOME") ?: System.getenv("HOME"), ".local", "share", "Mindustry")
            OS.Mac -> FileAt(System.getenv("HOME"), "Library", "Application Support", "Mindustry")
        }
    }
}