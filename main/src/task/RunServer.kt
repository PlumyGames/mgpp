package io.github.liplum.mindustry

import org.gradle.api.tasks.*

open class RunServer : RunMindustryAbstract() {

    @TaskAction
    override fun exec() {
        val dataDir = dataDir.get().resolveDir(this) ?: temporaryDir.resolve(name)
        if (dataDir.isDirectory) {
            // TODO: Record the mod signature.
            // TODO: Don't always delete all.
            dataDir.deleteRecursively()
        }
        dataDir.mkdirs()
        val modsFolder = dataDir.resolve("config").resolve("mods")
        for (modFile in mods) {
            if (modFile.isFile) {
                modFile.copyTo(modsFolder.resolve(modFile.name), overwrite = true)
            } else {
                logger.warn("Mod<$modFile> doesn't exist.")
            }
        }
        standardInput = System.`in`
        args = listOf(mindustryFile.get().absolutePath) + startupArgs.get()
        workingDir = dataDir
        logger.lifecycle("Run server in $dataDir.")
        // run Mindustry
        super.exec()
    }
}