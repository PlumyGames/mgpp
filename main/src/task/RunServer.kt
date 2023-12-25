package io.github.liplum.mindustry

import org.gradle.api.tasks.*

open class RunServer : RunMindustryAbstract() {
    init {
        mainClass.convention(R.mainClass.server)
    }

    @TaskAction
    override fun exec() {
        val dataDir = dataDir.get().resolveDir(this, GameSideType.Server) ?: temporaryDir.resolve(name)
        logger.lifecycle("Run server in $dataDir.")
        dataDir.mkdirs()
        loadMods(dataDir.resolve("mods"))
        standardInput = System.`in`
        args = listOf(gameFile.get().absolutePath) + startupArgs.get()
        workingDir = dataDir
        logger.lifecycle("Run server in $dataDir.")
        // run Mindustry
        super.exec()
    }
}