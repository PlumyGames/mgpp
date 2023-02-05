package io.github.liplum.mindustry

import io.github.liplum.dsl.fileProp
import io.github.liplum.dsl.prop
import io.github.liplum.mindustry.ModMeta
import io.github.liplum.mindustry.ModMeta.Companion.toHjson
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

open class ModHjsonGenerate : DefaultTask() {
    val modMeta = project.prop<ModMeta>()
        @Input get
    val outputHjson = project.fileProp()
        @OutputFile get
    @TaskAction
    fun generate() {
        val modHjson = outputHjson.get()
        modHjson.parentFile.mkdirs()
        modHjson.writeText(modMeta.get().toHjson())
        logger.info("ModHjson is generated at ${modHjson.absolutePath} .")
    }
}