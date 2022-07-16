package io.github.liplum.mindustry.task

import io.github.liplum.dsl.*
import io.github.liplum.mindustry.Mgpp
import org.gradle.api.tasks.*
import kotlin.collections.set

open class RunMindustry : JavaExec() {
    val mindustryFile = project.configurationFileCollection()
        @InputFiles get
    val dataDir = project.dirProp()
        @Optional @Input get
    val forciblyClear = project.boolProp()
        @Optional @Input get
    val dataModsPath = project.stringProp()
        @Input get
    val startupArgs = project.stringsProp()
        @Input @Optional get
    val outputtedMods = project.configurationFileCollection()
        @InputFiles get
    val modsWorkWith = project.configurationFileCollection()
        @InputFiles get
    init {
        mainClass.set("-jar")
        forciblyClear.convention(false)
        dataDir.convention(project.dirProv {
            temporaryDir.resolve("data")
        })
    }
    @TaskAction
    override fun exec() {
        val data = dataDir.asFile.get()
        data.mkdirs()
        val mods = data.resolve(dataModsPath.get())
        if (forciblyClear.get()) {
            mods.deleteRecursively()
        }
        mods.mkdirs()
        val workWith = modsWorkWith.files
        workWith.mapFilesTo(mods)
        logger.info("Copied mods working with[${workWith.joinToString(",") { it.name }}] into task $name")
        val output = outputtedMods.files
        logger.info("Copied outputted mods[${output.joinToString(",") { it.name }}] into task $name")
        outputtedMods.mapFilesTo(mods)
        standardInput = System.`in`
        args = listOf(mindustryFile.singleFile.absolutePath) + startupArgs.get()
        environment[Mgpp.MindustryDataDirEnv] = data.absoluteFile
        workingDir = data
        // run Mindustry
        super.exec()
    }
}