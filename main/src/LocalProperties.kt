package io.github.liplum.mindustry

import org.gradle.api.Project
import java.util.*

object LocalProperties {
    @JvmStatic
    private var _properties: Properties? = null
    val Project.localProperties: Properties
        get() = _properties ?: load()

    private fun Project.load(): Properties {
        val properties = Properties()
        val file = rootDir.resolve("local.properties")
        if (file.exists()) {
            file.inputStream().use { properties.load(it) }
            logger.info("local.properties was found.")
        }
        _properties = properties
        return properties
    }

    val Project.localProp: PropertiesSpec
        get() = PropertiesSpec(localProperties)

    /**
     * The cache should be clear when plugin starts being applied
     */
    fun clearCache(project: Project? = null) {
        _properties = null
        project?.logger?.info("local.properties cache was cleared.")
    }
}

@JvmInline
value class PropertiesSpec(
    val properties: Properties,
) {
    operator fun get(key: String): String? = properties.getProperty(key)
}