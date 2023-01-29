package io.github.liplum.mindustry.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

open class CleanMindustryDownloadCache : DefaultTask() {
    @TaskAction
    fun clean() {
        val userHome = System.getProperty("user.home")
        val cacheFolder = File(userHome).resolve(".gradle").resolve("mindustry-mgpp")
        if (cacheFolder.isDirectory) {
            cacheFolder.deleteRecursively()
        }
    }
}