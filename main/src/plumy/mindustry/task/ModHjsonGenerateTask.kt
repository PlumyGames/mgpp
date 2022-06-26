package plumy.mindustry.task

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import plumy.dsl.FileProp
import plumy.mindustry.ModMeta
import plumy.mindustry.ModMeta.Companion.toHjson

abstract class ModHjsonGenerateTask : DefaultTask() {
    abstract val modMeta: Property<ModMeta>
        @Input get
    abstract val outputHjson: FileProp
        @OutputFile get
    @TaskAction
    fun generate() {
        val modHjson = outputHjson.get()
        modHjson.parentFile.mkdirs()
        modHjson.writeText(modMeta.get().toHjson())
        logger.info("ModHjson is generated at ${modHjson.absolutePath} .")
    }
}