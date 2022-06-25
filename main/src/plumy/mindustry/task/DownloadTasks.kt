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
import plumy.mindustry.GitHubAsset

open class DownloadTask : DefaultTask() {
    val assets = project.prop<GitHubAsset>()
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
        assets.get().url.copyTo(outputPath.get())
    }
}

var DownloadTask.Assets: GitHubAsset?
    get() = assets.orNull
    set(value) {
        assets.set(value)
    }

