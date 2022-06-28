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
        val naming = args["NameRule"]?.let { NameRule.valueOf(it) } ?: NameRule.Camel
        val modName = args["ModName"]?:""
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
            val varName = naming.rename(fileName.split("-"))
            file += "public static $tr $varName;\n"
            val trName = if(modName.isNotBlank()) "$modName-$fileName" else fileName
            loadFuncBlock += "$varName = $arcCore.atlas.find(\"$trName\");\n"
        }
        loadFuncBlock.append("}\n")
        file += loadFuncBlock.toString()
    }
}
