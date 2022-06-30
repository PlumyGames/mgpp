package plumy.mindustry

import org.gradle.api.Action
import org.gradle.api.Project
import plumy.dsl.fileProp
import plumy.dsl.listProp
import plumy.dsl.stringProp
import java.io.File
import java.io.Serializable

open class MindustryAssetsExtension(
    target: Project,
) {
    val assetsRoot = target.fileProp().apply {
        convention(MindustryPlugin.DefaultEmptyFile)
    }
    val qualifiedName = target.stringProp().apply {
        convention("default")
    }
    val generators = HashMap<String, IResourceClassGenerator>(
        mapOf(
            "DefaultSprite" to SpritesGenerator,
            "DefaultSound" to SoundsGenerator,
        )
    )
    val args = HashMap<String, String>()
    val batches = target.listProp<AssetBatch>().apply {
        convention(HashSet())
    }
    val root = AssetRootSpec()
    val sprites = BatchType(
        group = "sprites",
        className = "Sprite",
        generator = "DefaultSprite"
    )
    val sounds = BatchType(
        group = "sounds",
        className = "Sound",
        generator = "DefaultSound"
    )
    val shaders = BatchType(
        group = "shaders",
        className = "Shader",
    )
    val bundles = BatchType(
        group = "bundles",
        className = "Bundle",
    )

    fun rootAt(path: String) {
        assetsRoot.set(File(path))
    }

    fun getGenerator(name: String) =
        generators[name] ?: IResourceClassGenerator.Empty

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
    fun BatchType(
        group: String = "",
        className: String = "",
        generator: String = "",
    ): AssetBatchType {
        val type = AssetBatchType(group, className, generator).apply {
            self = this@MindustryAssetsExtension
        }
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
}

data class AssetBatchType(
    var group: String = "",
    var className: String = "",
    var generator: String = "",
) : Serializable {
    var self: MindustryAssetsExtension? = null
    // For Groovy
    fun add(
        config: Action<AssetBatch>,
    ) {
        val self = self ?: return
        val newBatch = AssetBatch(this)
        config.execute(newBatch)
        self.batches.add(newBatch)
    }
    // For Kotlin
    inline operator fun invoke(
        config: AssetBatch.() -> Unit,
    ) {
        val self = self ?: return
        val newBatch = AssetBatch(this).apply(config)
        self.batches.add(newBatch)
    }
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
