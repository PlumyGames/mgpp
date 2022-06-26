package plumy.mindustry.task

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.*
import plumy.dsl.*
import plumy.mindustry.Meta

open class RunMindustry : DefaultTask() {
    val mainClass = project.stringProp()
        @Input get
    val dataDir = project.dirProp()
        @Optional @InputDirectory get
    val dataModsPath = project.stringProp()
        @Input get
    val outputtedMods = project.configurationFileCollection()
        @InputFiles get
    val modsWorkWith = project.configurationFileCollection()
        @InputFiles get
    val workingDir = project.dirProp()
        @Optional @Input get
    val classPath = project.configurationFileCollection()
        @InputFiles get

    init {
        project.func {
            resolveDefaultDataDir(dataDir)
            workingDir.convention(dirProv(temporaryDir))
        }
    }

    fun dataOnTemporary() {
        dataDir.set(temporaryDir.resolve("data").apply {
            mkdirs()
        })
    }
    @TaskAction
    fun run() = project.func {
        val data = dataDir.get().asFile
        data.mkdirs()
        val mods = data.resolve(dataModsPath.get())
        delete(mods)
        mods.mkdirs()
        modsWorkWith.mapFilesTo(mods, overwrite = false)
        outputtedMods.mapFilesTo(mods, overwrite = true)
        javaexec {
            it.mainClass.set(mainClass)
            it.classpath = classPath
            it.standardInput = System.`in`
            it.workingDir = workingDir.get().asFile
            it.environment[Meta.MindustryDataDirEnv] = dataDir.get().asFile.absoluteFile
        }
    }
}

var RunMindustry.MainClass: String
    get() = mainClass.getOrElse("")
    set(value) {
        mainClass.set(value)
    }

fun Project.resolveDefaultDataDir(dataDir: DirProp) {
    when (getOs()) {
        OS.Unknown -> logger.warn("Can't recognize your operation system.")
        OS.Windows -> dataDir.convention(dirProv(FileAt(System.getenv("AppData"), "Mindustry")))
        OS.Linux -> dataDir.set(
            dirProv(
                FileAt(System.getenv("XDG_DATA_HOME") ?: System.getenv("HOME"), ".local", "share", "Mindustry")
            )
        )
        OS.Mac -> dataDir.set(dirProv(FileAt(System.getenv("HOME"), "Library", "Application Support", "Mindustry")))
    }
}
