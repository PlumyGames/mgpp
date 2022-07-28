package io.github.liplum.mindustry.task

import io.github.liplum.dsl.boolProp
import io.github.liplum.dsl.copyTo
import io.github.liplum.dsl.prop
import io.github.liplum.mindustry.IGameLocation
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

open class Download : DefaultTask() {
    val location = project.prop<IGameLocation>()
        @Input get
    val overwrite = project.boolProp()
        @Optional @Input get
    val keepOthers = project.boolProp()
        @Optional @Input get
    val outputFile
        @OutputFile get() = temporaryDir.resolve(location.get().fileName)

    init {
        overwrite.convention(false)
        keepOthers.convention(true)
        outputs.upToDateWhen {
            outputFile.exists()
        }
    }
    @TaskAction
    fun download() {
        // Download is a very expensive task, it should detect whether the file exists.
        val asset = location.get()
        val downloadTask = asset.toDownloadLocation()
        val output = outputFile
        if (!keepOthers.get()) {
            ((temporaryDir.listFiles() ?: emptyArray()).toList() - output).forEach {
                it.delete()
            }
        }
        if (!output.exists() || overwrite.get()) {
            logger.lifecycle("Downloading $asset from ${asset.fileName} .")
            downloadTask.openInputStream().use { it.copyTo(output) }
            logger.info("Downloaded ${asset.fileName}}.")
        } else {
            logger.info("$asset has been already downloaded at ${output.absolutePath} , so skip it.")
        }
    }
}
