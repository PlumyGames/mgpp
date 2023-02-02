package io.github.liplum.mindustry.task

import io.github.liplum.dsl.*
import io.github.liplum.dsl.dirProp
import io.github.liplum.dsl.dirProv
import io.github.liplum.dsl.fileProp
import io.github.liplum.dsl.stringsProp
import io.github.liplum.mindustry.R
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.tasks.*

open class RunClient : RunMindustryAbstract() {
    init {
        dataDir.convention(project.dirProv {
            temporaryDir.resolve("data")
        })
    }
    @TaskAction
    override fun exec() {
        val data = dataDir.asFile.get()
        data.mkdirs()
        standardInput = System.`in`
        args = listOf(mindustryFile.get().absolutePath) + startupArgs.get()
        environment[R.env.mindustryDataDir] = data.absoluteFile
        if (Os.isFamily(Os.FAMILY_MAC)) {
            // Lwjgl3 application requires it to run on macOS
            jvmArgs = (jvmArgs ?: mutableListOf()) + "-XstartOnFirstThread"
        }
        workingDir = data
        // run Mindustry
        super.exec()
    }
}