package io.github.liplum.mindustry.task

import io.github.liplum.dsl.dirProp
import io.github.liplum.dsl.dirProv
import io.github.liplum.dsl.fileProp
import io.github.liplum.dsl.stringsProp
import org.gradle.api.tasks.*

open class RunServer : JavaExec() {
    val mindustryFile = project.fileProp()
        @InputFile get
    val dataDir = project.dirProp()
        @Optional @Input get
    val startupArgs = project.stringsProp()
        @Input @Optional get

    init {
        mainClass.set("-jar")
        dataDir.convention(project.dirProv {
            temporaryDir.resolve("data")
        })
    }
    @TaskAction
    override fun exec() {

    }
}