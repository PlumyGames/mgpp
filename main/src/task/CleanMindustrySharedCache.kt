package io.github.liplum.mindustry

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class CleanMindustrySharedCache : DefaultTask() {
    @TaskAction
    fun clean() {
        try {
            SharedCache.cleanCache()
        } catch (e: Exception) {
            logger.info(e.localizedMessage, e)
        }
    }
}