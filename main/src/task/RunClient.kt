package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import org.gradle.api.tasks.*

open class RunClient : RunMindustryAbstract() {
    init {
        mainClass.convention(R.mainClass.desktop)
    }

    @TaskAction
    override fun exec() {
        val dataDir = dataDir.get().resolveDir(this, GameSideType.Client) ?: temporaryDir.resolve(name)
        logger.lifecycle("Run client in $dataDir.")
        environment[R.env.mindustryDataDir] = dataDir.absoluteFile
        workingDir = dataDir
        dataDir.mkdirs()
        loadMods(dataDir.resolve("mods"))
        standardInput = System.`in`
        args = listOf(gameFile.get().absolutePath) + startupArgs.get()
        if (getOs() == OS.MacOS) {
            // Lwjgl3 application requires it to run on macOS
            jvmArgs = (jvmArgs ?: mutableListOf()) + "-XstartOnFirstThread"
        }
        logger.lifecycle("Run client in ${this.dataDir}.")
        // run Mindustry
        super.exec()
    }
}