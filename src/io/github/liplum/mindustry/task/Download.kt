package io.github.liplum.mindustry.task

import io.github.liplum.dsl.*
import io.github.liplum.mindustry.IDownloadLocation
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

open class Download : DefaultTask() {
    val location = project.prop<IDownloadLocation>()
        @Input get
    val overwrite = project.prop<Boolean>()
        @Optional @Input get
    val keepOthers = project.prop<Boolean>()
        @Optional @Input get
    val outputFileName = project.stringProp()
        @Input @Optional get
    val outputFile
        @OutputFile get() = temporaryDir.resolve(outputFileName.get())

    init {
        outputFileName.convention(project.provider {
            location.get().name
        })
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
        val output = outputFile
        if(!keepOthers.get()){
            ((temporaryDir.listFiles()?: emptyArray()).toList() - output).forEach {
                it.delete()
            }
        }
        if (!output.exists() || overwrite.get()) {
            logger.info("Downloading $asset from ${asset.path}. ")
            asset.openInputStream().use { it.copyTo(output) }
            logger.info("Downloaded ${asset.name}.")
        } else {
            logger.info("$asset has been already downloaded at ${output.absolutePath} , so skip it.")
        }
    }
}

var Download.Assets: IDownloadLocation?
    get() = location.orNull
    set(value) {
        location.set(value)
    }

