package io.github.liplum.mindustry

import org.gradle.api.Project

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
