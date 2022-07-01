package plumy.mindustry.task

import org.gradle.api.DefaultTask
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.*
import plumy.dsl.*
import plumy.mindustry.IResourceClassGenerator
import plumy.mindustry.IResourceClassGenerator.Empty

open class ResourceClassGenerate : DefaultTask() {
    val resources = project.configurationFileCollection()
        @InputFiles get
    val className = project.stringProp()
        @Input get
    val args: MapProperty<String, String> =
        project.objects.mapProperty(String::class.java, String::class.java)
        @Input @Optional get
    val generated = project.fileProp()
        @OutputFile get
    var generator: IResourceClassGenerator = Empty
        @Internal get

    init {
        generated.convention(project.provider {
            temporaryDir.resolve("generated")
        })
    }
    @TaskAction
    fun generate() {
        generated.get().ensure().outputStream().use { file ->
            val args = args.get()
            val className = className.get()
            file += "public static final class $className {\n"
            generator.generateClass(resources.files, args, file)
            file += "}\n"
        }
    }
}
