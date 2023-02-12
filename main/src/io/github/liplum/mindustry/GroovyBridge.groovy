package io.github.liplum.mindustry

import org.gradle.api.Project

/**
 * For attaching extension method of Kotlin to Groovy.
 * But Groovy and Kotlin Failed to interact each other.
 */
class GroovyBridge {
    static def attach(Project project) {
        project.metaClass.mindustryRepo = {
            return Class.forName("io.github.liplum.mindustry.BuildScript").mindustryRepo(project.getRepositories())
        }
        project.metaClass.importMindustry = {
            Class.forName("io.github.liplum.mindustry.BuildScript").importMindustry(project)
        }
    }
}
