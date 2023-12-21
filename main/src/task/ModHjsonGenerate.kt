package io.github.liplum.mindustry

import io.github.liplum.dsl.ensureParentDir
import io.github.liplum.dsl.fileProp
import io.github.liplum.dsl.prop
import io.github.liplum.mindustry.ModMeta.Companion.toHjson
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

open class ModHjsonGenerate : DefaultTask() {
    val modMeta = project.prop<ModMeta>()
        @Input get
    val output = project.fileProp()
        @OutputFile get

    @TaskAction
    fun generate() {
        val output = output.get()
        output.ensureParentDir()
        val modMetaText = modMeta.get().toHjson()
        output.writeText(modMetaText)
        logger.info("mod.hjson was generated at ${output.absolutePath} .")
    }
}