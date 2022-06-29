package plumy.mindustry

import org.gradle.api.Action
import org.gradle.api.Project
import plumy.dsl.listProp
import plumy.dsl.stringProp
import java.io.File
import java.io.Serializable

open class MindustryAssetExtension(
    target: Project,
) {
    val qualifiedName = target.stringProp().apply {
        convention("default")
    }
    val generators = HashMap<String, IResourceClassGenerator>(
        mapOf("DefaultSprite" to SpritesGenerator)
    )

    fun getGenerator(name: String) =
        generators[name] ?: IResourceClassGenerator.Empty

    val batches = target.listProp<AssetBatch>().apply {
        convention(HashSet())
    }

    inline fun sounds(
        configBatch: AssetBatch.() -> Unit,
    ) {
        val newBatch = AssetBatch().also {
            it.group = "sounds"
            it.className = "Sound"
        }
        newBatch.configBatch()
        batches.add(newBatch)
    }

    inline fun sprites(
        configBatch: AssetBatch.() -> Unit,
    ) {
        val newBatch = AssetBatch().also {
            it.group = "sprites"
            it.className = "Sprite"
            it.generator = "DefaultSprite"
        }
        newBatch.configBatch()
        batches.add(newBatch)
    }

    inline fun bundles(
        configBatch: AssetBatch.() -> Unit,
    ) {
        val newBatch = AssetBatch().also {
            it.group = "bundles"
            it.className = "Bundle"
        }
        newBatch.configBatch()
        batches.add(newBatch)
    }

    inline fun shaders(
        configBatch: AssetBatch.() -> Unit,
    ) {
        val newBatch = AssetBatch().also {
            it.group = "shaders"
            it.className = "Shader"
        }
        newBatch.configBatch()
        batches.add(newBatch)
    }

    fun sounds(
        configBatch: Action<AssetBatch>,
    ) {
        val newBatch = AssetBatch().also {
            it.group = "sounds"
            it.className = "Sound"
        }
        configBatch.execute(newBatch)
        batches.add(newBatch)
    }

    fun sprites(
        configBatch: Action<AssetBatch>,
    ) {
        val newBatch = AssetBatch().also {
            it.group = "sprites"
            it.className = "Sprite"
            it.generator = "DefaultSprite"
        }
        configBatch.execute(newBatch)
        batches.add(newBatch)
    }

    fun bundles(
        configBatch: Action<AssetBatch>,
    ) {
        val newBatch = AssetBatch().also {
            it.group = "bundles"
            it.className = "Bundle"
        }
        configBatch.execute(newBatch)
        batches.add(newBatch)
    }

    fun shaders(
        configBatch: Action<AssetBatch>,
    ) {
        val newBatch = AssetBatch().also {
            it.group = "shaders"
            it.className = "Shader"
        }
        configBatch.execute(newBatch)
        batches.add(newBatch)
    }

    inline operator fun String.invoke(
        configBatch: AssetBatch.() -> Unit,
    ) {
        val newBatch = AssetBatch()
        newBatch.configBatch()
        batches.add(newBatch)
    }

    fun BatchType(config: AssetBatchType.() -> Unit): AssetBatchType {
        val type = AssetBatchType(this)
        type.config()
        return type
    }
}

data class AssetBatch(
    var group: String = "",
    var className: String = "",
    var generator: String = "",
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

class AssetBatchType(
    val extension: MindustryAssetExtension,
    var group: String = "",
    var className: String = "",
) {
    inline operator fun invoke(
        configBatch: AssetBatch.() -> Unit,
    ) {
        val newBatch = AssetBatch().also {
            it.group = group
            it.className = className
        }
        newBatch.configBatch()
        extension.batches.add(newBatch)
    }
}

data class AssetBatchGroup(
    var name: String = "",
    var className: String = "",
    var generator: String = "",
)

fun List<AssetBatch>.resolveBatches():
        Map<AssetBatchGroup, List<AssetBatch>> =
    this.groupBy { AssetBatchGroup(it.group, it.className, it.generator) }
