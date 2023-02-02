package io.github.liplum.mindustry.task

import io.github.liplum.dsl.dirProp
import io.github.liplum.dsl.dirProv
import io.github.liplum.dsl.fileProp
import io.github.liplum.dsl.stringsProp
import org.gradle.api.tasks.*

open class RunServer : RunMindustryAbstract() {
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
        workingDir = data
        // run Mindustry
        super.exec()
    }
}