package io.github.liplum.mindustry.task

import org.gradle.api.tasks.*

open class RunServer : RunMindustryAbstract() {

    @TaskAction
    override fun exec() {
        val data = dataDir.get().resolveDir(this) ?: temporaryDir.resolve(name)
        if (data.isDirectory) {
            // TODO: Record the mod signature.
            // TODO: Don't always delete all.
            data.deleteRecursively()
        }
        data.mkdirs()
        val modsFolder = data.resolve("config").resolve("mods")
        for (modFile in mods) {
            if (modFile.isFile) {
                modFile.copyTo(modsFolder.resolve(modFile.name), overwrite = true)
            } else {
                logger.warn("Mod<$modFile> doesn't exist.")
            }
        }
        standardInput = System.`in`
        args = listOf(mindustryFile.get().absolutePath) + startupArgs.get()
        workingDir = data
        // run Mindustry
        super.exec()
    }
}