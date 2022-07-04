package io.github.liplum.mindustry.task

import io.github.liplum.dsl.*
import io.github.liplum.mindustry.GenerateContext
import io.github.liplum.mindustry.IResourceClassGenerator
import io.github.liplum.mindustry.ResourceClassGeneratorRegistry
import org.gradle.api.DefaultTask
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.*

open class ResourceClassGenerate : DefaultTask() {
    val resources = project.configurationFileCollection()
        @InputFiles get
    val className = project.stringProp()
        @Input get
    val args: MapProperty<String, String> =
        project.objects.mapProperty(String::class.java, String::class.java)
        @Input @Optional get
    val generator = project.stringProp()
        @Input @Optional get
    val generated = project.fileProp()
        @OutputFile get

    init {
        generated.convention(project.provider {
            temporaryDir.resolve("generated")
        })
    }
    @TaskAction
    fun generate() {
        val gen = if (!generator.isPresent || generator.get().isBlank()) {
            logger.warn("Doesn't find any generator of ${this.name}, please at least specify one.")
            IResourceClassGenerator.Empty
        }
        else {
            val generatorName = generator.getOrElse("")
            ResourceClassGeneratorRegistry[generatorName].apply {
                if(this == IResourceClassGenerator.Empty)
                    logger.warn("Can't find ${generatorName}, please check the typo")
            }
        }
        generated.get().ensure().outputStream().use { file ->
            val args = args.get()
            val className = className.get()
            file += "public static final class $className {\n"
            val context = GenerateContext(
                name = className,
                logger = logger,
                resources = resources.files,
                args = args,
                file = file)
            gen.generateClass(context)
            file += "}\n"
        }
    }
}
