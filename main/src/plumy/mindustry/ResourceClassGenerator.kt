package plumy.mindustry

import plumy.dsl.getFilesRecursive
import plumy.dsl.plusAssign
import java.io.File
import java.io.OutputStream

interface IResourceClassGenerator {
    fun generateClass(resources: Collection<File>, args: Map<String, String>, file: OutputStream)

    companion object Empty : IResourceClassGenerator {
        override fun generateClass(resources: Collection<File>, args: Map<String, String>, file: OutputStream) {
        }
    }
}

object SpritesGenerator : IResourceClassGenerator {
    override fun generateClass(
        resources: Collection<File>,
        args: Map<String, String>,
        file: OutputStream,
    ) {
        val naming = args["TargetNameRule"]?.let { NameRule.valueOf(it) } ?: NameRule.Camel
        val rNaming = args["ResourceNameRule"]?.let { NameRule.valueOf(it) } ?: NameRule.Kebab
        val modName = args["ModName"] ?: ""
        val loadFunc = args["LoadFunctionName"] ?: "load"
        val arcCore = args["Class[arc.Core]"] ?: "arc.Core"
        val tr = args["Class[TextureRegion]"] ?: "arc.graphics.g2d.TextureRegion"
        val allFiles = resources.flatMap { it.getFilesRecursive() }
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

object SoundsGenerator : IResourceClassGenerator {
    override fun generateClass(
        resources: Collection<File>,
        args: Map<String,
                String>,
        file: OutputStream,
    ) {
        val naming = args["TargetNameRule"]?.let { NameRule.valueOf(it) } ?: NameRule.Camel
        val rNaming = args["ResourceNameRule"]?.let { NameRule.valueOf(it) } ?: NameRule.Kebab
        val loadFunc = args["LoadFunctionName"] ?: "load"
        val arcCore = args["Class[arc.Core]"] ?: "arc.Core"
        val arcSound = args["Class[arc.audio.Sound]"] ?: "arc.audio.Sound"
        val desc = args["Class[arc.assets.AssetDescriptor]"] ?: "arc.assets.AssetDescriptor"
        val para = args["Class[arc.assets.loaders.SoundLoader.SoundParameter]"] ?: "arc.assets.loaders.SoundLoader.SoundParameter"
        val allFiles = resources.flatMap { it.getFilesRecursive() }
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

