package io.github.liplum.mindustry

import org.gradle.api.Project
import java.util.*

object LocalProperties {
    @JvmStatic
    private var all: Properties? = null
    val Project.localProperties: Properties
        get() = all ?: load()

    fun clearCache(project: Project? = null) {
        all = null
        project?.logger?.info("local.properties cache was cleared.")
    }

    private fun Project.load(): Properties {
        val properties = Properties()
        val file = rootDir.resolve("local.properties")
        if (file.exists()) {
            file.inputStream().use { properties.load(it) }
            logger.info("local.properties was found.")
        } else {
            logger.info("local.properties not found.")
        }
        all = properties
        return properties
    }
}