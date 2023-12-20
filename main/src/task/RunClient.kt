package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.tasks.*

open class RunClient : RunMindustryAbstract() {

    @TaskAction
    override fun exec() {
        val dataDir = dataDir.get().resolveDir(this, GameSideType.Client) ?: temporaryDir.resolve(name)
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
                createSymbolicLinkOrCopy(link = modsFolder.resolve(modFile.name), target = modFile)
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
        logger.lifecycle("Run client in ${this.dataDir}.")
        // run Mindustry
        super.exec()
    }
}