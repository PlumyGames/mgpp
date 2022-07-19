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
        # Whether to keep all projects generate a fat jar
        # options: `true`, `false` 
        # mgpp.run.enableFatJar=false
        
        # Overwrite the data directory when running Mindustry client
        # options: `default`, `temp` or a local folder 
        # mgpp.run.dataDir=
        
        # Whether to clear other files in your "Mindustry/mods" folder
        # options: `true`, `false` 
        # mgpp.run.forciblyClear=true
        
        # Overwrite your Mindustry client location to a local file
        # mgpp.client.location=
        
        # Overwrite your Mindustry server location to a local file
        # mgpp.server.location=
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