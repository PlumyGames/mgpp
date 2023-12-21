package io.github.liplum.mindustry

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Delete the shared cache
 */
open class CleanSharedCache : DefaultTask() {
    @TaskAction
    fun clean() {
        try {
            SharedCache.cleanCache()
        } catch (e: Exception) {
            logger.info(e.localizedMessage, e)
        }
    }
}

/**
 * Validate the shared cache:
 * 1. Check out of date
 */
open class ValidateSharedCache : DefaultTask() {
    @TaskAction
    fun validate() {

    }
}