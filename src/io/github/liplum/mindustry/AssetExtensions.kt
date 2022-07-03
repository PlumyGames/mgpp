package io.github.liplum.mindustry

import io.github.liplum.dsl.fileProp
import io.github.liplum.dsl.listProp
import io.github.liplum.dsl.stringProp
import io.github.liplum.mindustry.MindustryAssetsExtension.AssetBatchType
import org.gradle.api.Action
import org.gradle.api.Project
import java.io.File
import java.io.Serializable

object ResourceClassGeneratorRegistry {
    @JvmStatic
    val all = HashMap<String, IResourceClassGenerator>(
        mapOf(
            "DefaultSprite" to SpritesGenerator,
            "DefaultSound" to SoundsGenerator,
        )
    )

    operator fun get(name: String): IResourceClassGenerator =
        all[name] ?: IResourceClassGenerator.Empty

    operator fun set(name: String, gen: IResourceClassGenerator) {
        all[name] = gen
    }
}

open class MindustryAssetsExtension(
    target: Project,
) {
    val assetsRoot = target.fileProp().apply {
        convention(MindustryPlugin.DefaultEmptyFile)
    }
    val qualifiedName = target.stringProp().apply {
        convention("default")
    }
    val generators = ResourceClassGeneratorRegistry.all
    val args = HashMap<String, String>()
    val batches = target.listProp<AssetBatch>().apply {
        convention(HashSet())
    }
    val icon = target.fileProp().apply {
        convention(target.rootDir.resolve("icon.png"))
    }
    val root = AssetRootSpec()
    val sprites = AssetBatchType(
        group = "sprites",
        className = "Sprite",
        generator = "DefaultSprite"
    )

    fun sprites(config: Action<AssetBatch>) = sprites.add(config)
    val sounds = AssetBatchType(
        group = "sounds",
        className = "Sound",
        generator = "DefaultSound"
    )

    fun sounds(config: Action<AssetBatch>) = sounds.add(config)
    val shaders = AssetBatchType(
        group = "shaders",
        className = "Shader",
    )

    fun shaders(config: Action<AssetBatch>) = shaders.add(config)
    val bundles = AssetBatchType(
        group = "bundles",
        className = "Bundle",
    )

    fun bundles(config: Action<AssetBatch>) = bundles.add(config)
    fun rootAt(path: String) {
        assetsRoot.set(File(path))
    }

    fun getGenerator(name: String) =
        ResourceClassGeneratorRegistry[name]

    fun setGenerator(name: String, gen: IResourceClassGenerator) {
        ResourceClassGeneratorRegistry[name] = gen
    }

    inline operator fun String.invoke(
        config: AssetBatchType.() -> Unit,
    ): AssetBatchType {
        val type = AssetBatchType()
        type.config()
        return type
    }

    fun BatchType(config: AssetBatchType.() -> Unit): AssetBatchType {
        val type = AssetBatchType()
        type.config()
        return type
    }

    inner class AssetRootSpec {
        infix fun at(path: String) {
            assetsRoot.set(File(path))
        }

        infix fun at(folder: File) {
            assetsRoot.set(folder)
        }
    }

    inner class AssetBatchType(
        var group: String = "",
        var className: String = "",
        var generator: String = "",
        var nameRule: NameRule = NameRule.Kebab,
    ) : Serializable {
        // For Groovy
        fun add(
            config: Action<AssetBatch>,
        ) {
            val newBatch = AssetBatch(this)
            config.execute(newBatch)
            batches.add(newBatch)
        }
        // For Kotlin
        inline operator fun invoke(
            config: AssetBatch.() -> Unit,
        ) {
            val newBatch = AssetBatch(this).apply(config)
            batches.add(newBatch)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as AssetBatchType

            if (group != other.group) return false
            if (className != other.className) return false
            if (generator != other.generator) return false

            return true
        }

        override fun hashCode(): Int {
            var result = group.hashCode()
            result = 31 * result + className.hashCode()
            result = 31 * result + generator.hashCode()
            return result
        }

        override fun toString(): String {
            return "AssetBatchType(group='$group', className='$className', generator='$generator')"
        }
    }

    val Pascal = NameRule.Pascal
    val Camel = NameRule.Camel
    val Snake = NameRule.Snake
    val AllCaps = NameRule.AllCaps
    val Kebab = NameRule.Kebab
    val Domain = NameRule.Domain
}

data class AssetBatch(
    var type: AssetBatchType,
    var enableGenClass: Boolean = false,
    var dir: File = File(""),
    var root: File = MindustryPlugin.DefaultEmptyFile,
    var dependsOn: ArrayList<Any> = ArrayList(),
) : Serializable {
    fun dependsOn(task: Any) {
        dependsOn.add(task)
    }

    fun rootAt(path: String) {
        root = File(path)
    }

    fun rootAt(file: File) {
        root = file
    }

    val noGenClass: Unit
        get() {
            enableGenClass = false
        }
    val genClass: Unit
        get() {
            enableGenClass = true
        }
}

fun List<AssetBatch>.resolveBatches():
        Map<AssetBatchType, List<AssetBatch>> =
    this.groupBy { it.type }
