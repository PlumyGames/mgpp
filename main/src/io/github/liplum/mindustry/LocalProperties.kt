package io.github.liplum.mindustry

import org.gradle.api.Project
import java.util.*

object LocalProperties {
    private var all: Properties? = null
    val Project.localProperties: Properties
        get() = all ?: load()

    private fun Project.load(): Properties {
        val properties = Properties()
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            file.inputStream().use { properties.load(it) }
        }
        all = properties
        return properties
    }
}