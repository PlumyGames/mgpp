package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.dsl.fileProp
import io.github.liplum.dsl.stringsProp
import io.github.liplum.mindustry.IDataDirLoc
import org.gradle.api.tasks.*

abstract class RunMindustryAbstract : JavaExec() {
    val mindustryFile = project.fileProp()
        @InputFile get
    val dataDir = project.prop<IDataDirLoc>()
        @Optional @Input get
    val startupArgs = project.stringsProp()
        @Input @Optional get
    val mods = project.configurationFileCollection()
        @InputFiles @Optional get

    init {
        mainClass.set("-jar")
    }
}