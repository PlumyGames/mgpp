package plumy.mindustry.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import plumy.dsl.copyTo
import plumy.dsl.fileProp
import plumy.dsl.prop
import plumy.dsl.tempFi
import plumy.mindustry.IDownloadLocation

open class DownloadTask : DefaultTask() {
    val assets = project.prop<IDownloadLocation>()
        @Input get
    val outputPath = project.fileProp()
        @Optional @OutputFile get

    init {
        outputPath.convention(project.provider {
            tempFi(assets.get().name)
        })
    }
    @TaskAction
    fun download() {
        // Download is a very expensive task, so this should
        val asset = assets.get()
        logger.info("Downloading ${asset.name} from ${asset}. ")
        asset.openInputStream().use { it.copyTo(outputPath.get()) }
        logger.info("Downloaded ${asset.name} from ${asset.path}.")
    }
}

var DownloadTask.Assets: IDownloadLocation?
    get() = assets.orNull
    set(value) {
        assets.set(value)
    }

