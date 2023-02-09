package io.github.liplum.mindustry

import org.gradle.api.Project
import java.util.*

object LocalProperties {
    @JvmStatic
    private var all: Properties? = null
    val Project.localProperties: Properties
        get() = all ?: load()
    val Project.local: PropertiesSpec
        get() = PropertiesSpec(localProperties)

    fun clearCache(project: Project? = null) {
        all = null
        project?.logger?.info("local.properties cache was cleared.")
    }

    val initialText = """
    """.trimIndent()

    private fun Project.load(): Properties {
        val properties = Properties()
        val file = rootDir.resolve("local.properties")
        if (file.exists()) {
            file.inputStream().use { properties.load(it) }
            logger.info("local.properties was found.")
        } else {
            file.writeText(initialText)
            logger.info("local.properties was created.")
        }
        all = properties
        return properties
    }
}
@JvmInline
value class PropertiesSpec(
    val properties: Properties,
) {
    operator fun get(key: String): String? = properties.getProperty(key)
}