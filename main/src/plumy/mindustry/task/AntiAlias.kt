package plumy.mindustry.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileType
import org.gradle.api.tasks.*
import org.gradle.api.tasks.compile.AbstractOptions
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import plumy.dsl.dirProp
import plumy.dsl.new
import plumy.mindustry.antiAliasing
import java.io.File

open class AntiAlias : DefaultTask() {
    val sourceDirectory = project.dirProp()
        @Incremental @InputDirectory get
    val destinationDirectory = project.dirProp()
        @OutputDirectory get
    val options: AntiAliasingOptions = new()
        @Input @Optional get
    @TaskAction
    fun process(inputs: InputChanges) {
        if (options.isIncremental) {
            preformIncrementalAA(inputs)
        } else {
            performFullAA()
        }
    }

    protected fun performFullAA() {
        val dest = destinationDirectory.asFile.get()
        logger.info("To full anti-alias textures, ${dest.absolutePath} will be deleted before.")
        dest.deleteRecursively()
        dest.mkdirs()
        logger.info("To full anti-alias textures in ${sourceDirectory.asFile.get().absolutePath}")
        sourceDirectory.asFileTree.performAA()
    }

    protected fun preformIncrementalAA(inputs: InputChanges) {
        logger.info("Anti-aliasing textures incrementally in ${sourceDirectory.get().asFile.absolutePath} .")
        inputs.getFileChanges(sourceDirectory).mapNotNull {
            if (it.fileType == FileType.DIRECTORY) return@mapNotNull null
            val changedInDest = sourceDirectory.file(it.normalizedPath).get().asFile
            if (it.changeType == ChangeType.REMOVED) {
                logger.info("${it.file.absolutePath} has removed after last building.")
                changedInDest.delete()
                null
            } else {
                val fi = it.file
                if (fi.extension == "png") fi
                else null
            }
        }.performAA()
    }
    /**
     * @receiver an iterable of all source textures to be anti-aliased
     */
    protected fun Iterable<File>.performAA() {
        val sourceRoot = sourceDirectory.asFile.get()
        val destDir = destinationDirectory.asFile.get()
        this.forEach {
            val relative = it.normalize().relativeTo(sourceRoot)
            val to = destDir.resolve(relative)
            to.parentFile.mkdirs()
            logger.info("AntiAlias:${it.absolutePath} -> ${to.absolutePath}")
            antiAliasing(it, to)
        }
    }
}

open class AntiAliasingOptions : AbstractOptions() {
    var isIncremental = true
}