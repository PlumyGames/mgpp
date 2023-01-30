package io.github.liplum.mindustry

import io.github.liplum.dsl.func
import io.github.liplum.dsl.getFilesRecursive
import io.github.liplum.dsl.plusAssign
import org.gradle.api.logging.Logger
import java.io.File
import java.io.OutputStream

interface IGenerateContext {
    val name: String
    val logger: Logger
    val resources: Collection<File>
    val args: Map<String, String>
    val file: OutputStream
}

class GenerateContext(
    override val name: String,
    override val logger: Logger,
    override val resources: Collection<File>,
    override val args: Map<String, String>,
    override val file: OutputStream,
) : IGenerateContext

interface IResourceClassGenerator {
    fun generateClass(context: IGenerateContext)

    companion object Empty : IResourceClassGenerator {
        override fun generateClass(context: IGenerateContext) {
        }
    }
}

object DefaultSpritesGenerator : IResourceClassGenerator {
    override fun generateClass(context: IGenerateContext) = context.func {
        val naming = args["TargetNameRule"]?.let { NameRule.valueOf(it) } ?: NameRule.Camel
        val rNaming = args["ResourceNameRule"]?.let { NameRule.valueOf(it) } ?: NameRule.Kebab
        val modName = args["ModName"] ?: ""
        val loadFunc = args["LoadFunctionName"] ?: "load"
        val arcCore = args["Class[arc.Core]"] ?: "arc.Core"
        val tr = args["Class[TextureRegion]"] ?: "arc.graphics.g2d.TextureRegion"
        val allFiles = resources.flatMap { it.getFilesRecursive() }
        if (allFiles.isEmpty()) {
            logger.info("[RGenerate]No source for $name")
        }
        val fields = allFiles.map {
            it.nameWithoutExtension
        }.distinct()
        val loadFuncBlock = StringBuffer().apply {
            append("public static void $loadFunc(){\n")
        }
        fields.forEach { fileName ->
            val varName = naming.rename(rNaming.split(fileName))
            file += "public static $tr $varName;\n"
            val trName = if (modName.isNotBlank()) "$modName-$fileName" else fileName
            loadFuncBlock += "$varName = $arcCore.atlas.find(\"$trName\");\n"
        }
        loadFuncBlock.append("}\n")
        file += loadFuncBlock.toString()
    }
}

object DefaultSoundsGenerator : IResourceClassGenerator {
    override fun generateClass(context: IGenerateContext) = context.func {
        val naming = args["TargetNameRule"]?.let { NameRule.valueOf(it) } ?: NameRule.Camel
        val rNaming = args["ResourceNameRule"]?.let { NameRule.valueOf(it) } ?: NameRule.Kebab
        val loadFunc = args["LoadFunctionName"] ?: "load"
        val arcCore = args["Class[arc.Core]"] ?: "arc.Core"
        val arcSound = args["Class[arc.audio.Sound]"] ?: "arc.audio.Sound"
        val desc = args["Class[arc.assets.AssetDescriptor]"] ?: "arc.assets.AssetDescriptor"
        val para = args["Class[arc.assets.loaders.SoundLoader.SoundParameter]"] ?: "arc.assets.loaders.SoundLoader.SoundParameter"
        val allFiles = resources.flatMap { it.getFilesRecursive() }
        if (allFiles.isEmpty()) {
            logger.info("[RGenerate]No source for $name")
        }
        val fields = allFiles.map {
            it.name
        }.distinct()
        file += """
        protected static $arcSound loadSound(String soundName) {
            $arcSound sound = new $arcSound();
            $desc<?> desc = $arcCore.assets.load("sounds/" + soundName, $arcSound.class, new $para(sound));
            desc.errored = Throwable::printStackTrace;
            return sound;
        }
        """.trimIndent()
        file += "\n"
        val loadFuncBlock = StringBuffer().apply {
            append("public static void $loadFunc(){\n")
        }
        fields.forEach { fileFullName ->
            val fileName = fileFullName.substringBeforeLast(".")
            val varName = naming.rename(rNaming.split(fileName))
            file += "public static $arcSound $varName;\n"
            loadFuncBlock += "$varName = loadSound(\"$fileFullName\");\n"
        }
        loadFuncBlock.append("}\n")
        file += loadFuncBlock.toString()
    }
}
