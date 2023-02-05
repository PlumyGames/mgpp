package io.github.liplum.mindustry

import io.github.liplum.dsl.copyTo
import io.github.liplum.dsl.listProp
import io.github.liplum.dsl.prop
import io.github.liplum.mindustry.GitHubGameLoc
import io.github.liplum.mindustry.IGameLoc
import io.github.liplum.mindustry.IMod
import io.github.liplum.mindustry.LocalGameLoc
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction


open class ResolveGame : DefaultTask() {
    val location = project.prop<IGameLoc>()
        @Input get
    val mods = project.listProp<IMod>()
        @Input get
    val gameFile
        @OutputFile get() = location.get().resolveOutputFile()

    init {
        outputs.upToDateWhen {
            gameFile.exists()
        }
    }
    @TaskAction
    fun resolve() {
        when (val loc = location.get()) {
            is GitHubGameLoc -> resolveGitHubGameLoc(loc)
            is LocalGameLoc -> resolveLocalGameLoc(loc)
        }
    }

    fun resolveGitHubGameLoc(loc: GitHubGameLoc) {
        val output = loc.resolveOutputFile()
        if (output.isFile) return
        logger.lifecycle("Downloading $loc to $output...")
        try {
            loc.createDownloadLoc().openInputStream().use {
                it.copyTo(output)
            }
            logger.lifecycle("${loc.fileName} is downloaded.")
        } catch (e: Exception) {
            // now output is corrupted, delete it
            output.delete()
            throw e
        }
    }

    fun resolveLocalGameLoc(loc: LocalGameLoc) {
        val source = loc.resolveOutputFile()
        if (!source.isFile) {
            throw GradleException("Local game ($source) doesn't exists.")
        }
    }
}