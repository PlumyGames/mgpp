package io.github.liplum.mindustry.task

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileType
import org.gradle.api.tasks.*
import org.gradle.api.tasks.compile.AbstractOptions
import org.gradle.work.ChangeType
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import io.github.liplum.dsl.*
import io.github.liplum.mindustry.*
import java.io.File

open class AntiAlias : DefaultTask() {
    val sourceDirectory = project.dirProp()
        @InputDirectory get
    val inputs: ConfigurableFileCollection = project.files()
        @Incremental @InputFiles get
    val destinationDirectory = project.dirProp()
        @OutputDirectory get
    val filters = FileFilter.Set()
        @Internal get
    val options: AntiAliasingOptions = new()
        @Input @Optional get
    @TaskAction
    fun process(inputs: InputChanges) {
        if (options.isIncremental && inputs.isIncremental) {
            preformIncrementalAA(inputs)
        } else {
            performFullAA()
        }
    }

    protected fun performFullAA() {
        val dest = destinationDirectory.asFile.get()
        logger.info("Setup full anti-alias: ${dest.absolutePath} will be deleted before.")
        dest.deleteRecursively()
        dest.mkdirs()
        logger.info("Full anti-alias in ${sourceDirectory.asFile.get().absolutePath}")
        sourceDirectory.asFileTree.toList().performAA()
    }

    protected fun preformIncrementalAA(inputs: InputChanges) {
        logger.info("Anti-aliasing incrementally in ${sourceDirectory.get().asFile.absolutePath} .")
        inputs.getFileChanges(sourceDirectory.asFileTree).mapNotNull {
            if (it.fileType == FileType.DIRECTORY) return@mapNotNull null
            val changedInDest = sourceDirectory.file(it.normalizedPath).get().asFile
            if (it.changeType == ChangeType.REMOVED) {
                logger.info("${it.file.absolutePath} will be deleted due to the removal of source.")
                changedInDest.delete()
                null
            } else {
                it.file
            }
        }.toList().performAA()
    }
    /**
     * @receiver an iterable of all source textures to be anti-aliased
     */
    protected fun Collection<File>.performAA() {
        val sourceRoot = sourceDirectory.asFile.get()
        val destDir = destinationDirectory.asFile.get()
        this.stream().parallel().forEach {
            if (!it.extension.equals("png", ignoreCase = true)) {
                logger.info("$it isn't a png.")
                return@forEach
            }
            val relative = it.normalize().relativeTo(sourceRoot)
            val to = destDir.resolve(relative)
            to.parentFile.mkdirs()
            if (!filters.isAccept(it)) {
                if (to.exists()) {
                    to.delete()
                    logger.info("${to.absolutePath} will be deleted due to filter.")
                }
                return@forEach
            }
            logger.info("AntiAlias:${it.absolutePath} -> ${to.absolutePath}")
            try {
                antiAliasing(it, to)
            } catch (e: Exception) {
                logger.info("Can't anti alias ${it.absolutePath}", e)
            }
        }
    }

    fun clearFilter() {
        filters.clear()
    }

    fun removeFilter(filter: FileFilter) {
        filters -= filter
    }

    fun addFilter(filter: FileFilter) {
        filters += filter
    }
}

open class AntiAliasingOptions : AbstractOptions() {
    var isIncremental = true
}