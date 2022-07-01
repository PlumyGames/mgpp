package io.github.liplum.mindustry.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import io.github.liplum.dsl.*
import io.github.liplum.mindustry.*

open class Download : DefaultTask() {
    val location = project.prop<IDownloadLocation>()
        @Input get
    val overwrite = project.prop<Boolean>()
        @Optional @Input get
    val outputFile = project.fileProp()
        @Optional @OutputFile get

    init {
        outputFile.convention(project.provider {
            tempFi(location.get().name)
        })
        overwrite.convention(false)
        outputs.upToDateWhen {
            outputFile.get().exists()
        }
    }
    @TaskAction
    fun download() {
        // Download is a very expensive task, it should detect whether the file exists.
        val asset = location.get()
        val output = outputFile.get()
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

