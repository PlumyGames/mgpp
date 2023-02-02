package io.github.liplum.mindustry.task

import io.github.liplum.dsl.*
import io.github.liplum.dsl.dirProp
import io.github.liplum.dsl.fileProp
import io.github.liplum.dsl.stringsProp
import org.gradle.api.tasks.*

abstract class RunMindustryAbstract : JavaExec() {
    val mindustryFile = project.fileProp()
        @InputFile get
    val dataDir = project.dirProp()
        @Optional @Input get
    val startupArgs = project.stringsProp()
        @Input @Optional get
    val mods = project.configurationFileCollection()
        @InputFiles @Optional get

    init {
        mainClass.set("-jar")
    }
}