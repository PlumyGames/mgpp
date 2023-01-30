@file:JvmMultifileClass
@file:JvmName("ExtensionKt")
@file:Suppress("RemoveRedundantBackticks")

package io.github.liplum.mindustry

import io.github.liplum.dsl.*
import io.github.liplum.mindustry.*
import io.github.liplum.mindustry.MindustryAssetsExtension.AssetBatchType
import io.github.liplum.mindustry.task.GenerateRClass
import io.github.liplum.mindustry.task.GenerateResourceClass
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import java.io.File
import java.io.Serializable

/**
 * Retrieves the [mindustry][MindustryExtension] extension.
 */
val Project.`mindustryAssets`: MindustryAssetsExtension
    get() = (this as ExtensionAware).extensions.getByName(R.x.mindustryAssets) as MindustryAssetsExtension
/**
 * Configures the [mindustry][MindustryExtension] extension.
 */
fun Project.`mindustryAssets`(configure: Action<MindustryAssetsExtension>): Unit =
    (this as ExtensionAware).extensions.configure(R.x.mindustryAssets, configure)

object ResourceClassGeneratorRegistry {
    @JvmStatic
    val all = HashMap<String, IResourceClassGenerator>(
        mapOf(
            "DefaultSprite" to DefaultSpritesGenerator,
            "DefaultSound" to DefaultSoundsGenerator,
        )
    )
    /**
     * Get a resource generator or [IResourceClassGenerator.Empty] if that doesn't exist.
     */
    operator fun get(name: String): IResourceClassGenerator =
        all[name] ?: IResourceClassGenerator
    /**
     * Set a resource generator.
     */
    operator fun set(name: String, gen: IResourceClassGenerator) {
        all[name] = gen
    }
}

open class MindustryAssetsExtension(
    target: Project,
) {
    /**
     * The assets root of a mod only including a single `assets` folder
     */
    @JvmField
    val assetsRoot = target.fileProp().apply {
        convention(MindustryPlugin.DefaultEmptyFile)
    }
    /**
     * The qualified name of generated class,
     * such as `io.github.liplum.Mindustry`
     */
    val qualifiedName = target.stringProp().apply {
        convention("default")
    }
    /**
     * All resource class generators.
     * @see [IResourceClassGenerator]
     */
    val generators = ResourceClassGeneratorRegistry.all
    /**
     * The arguments used for generating.
     * @see GenerateResourceClass.args
     */
    val args = HashMap<String, String>()
    val batches = target.listProp<AssetBatch>().apply {
        convention(HashSet())
    }
    /**
     * Set the [assetsRoot] to [path]
     */
    fun rootAt(path: String) {
        assetsRoot.set(File(path))
    }
    /**
     * Set the [assetsRoot] to [file]
     */
    fun rootAt(file: File) {
        assetsRoot.set(file)
    }
    /**
     * Get a resource generator or [IResourceClassGenerator.Empty] if that doesn't exist.
     */
    fun getGenerator(name: String) =
        ResourceClassGeneratorRegistry[name]
    /**
     * Set a resource generator.
     */
    fun setGenerator(name: String, gen: IResourceClassGenerator) {
        ResourceClassGeneratorRegistry[name] = gen
    }
    /**
     * The icon of this mod to be included in `:jar` task.
     *
     * [Project.getRootDir]/icon.png as default
     */
    @JvmField
    val _icon = target.fileProp().apply {
        convention(
            findFileInOrder(
                target.proDir("icon.png"),
                target.rootDir("icon.png")
            )
        )
    }
    /**
     * Set the [_icon] to [file]
     */
    fun iconAt(file: File) {
        _icon.set(file)
    }
    /**
     * Set the [_icon] to [path]
     */
    fun iconAt(path: String) {
        _icon.set(File(path))
    }
    /**
     * A spec for configuring [assetsRoot].
     */
    // For Kotlin
    val root = AssetRootSpec()
    /**
     * A spec for configuring [icon].
     */
    // For Kotlin
    val icon = IconSpec()
    /**
     * The batch type of `sprites`:
     * - group: "sprites"
     * - className: "Sprite"
     * - generator: "DefaultSprite" -> [DefaultSpritesGenerator]
     */
    val sprites = AssetBatchType(
        group = "sprites",
        className = "Sprite",
        generator = "DefaultSprite"
    )
    /**
     * The batch type of `sprites`:
     * - group: "sprites"
     * - className: "Sprite"
     * - generator: "DefaultSprite" -> [DefaultSpritesGenerator]
     */
    fun sprites(config: Action<AssetBatch>) = sprites.add(config)
    /**
     * The batch type of `sounds`:
     * - group: "sounds"
     * - className: "Sound"
     * - generator: "DefaultSound" -> [DefaultSoundsGenerator]
     */
    val sounds = AssetBatchType(
        group = "sounds",
        className = "Sound",
        generator = "DefaultSound"
    )
    /**
     * The batch type of `sprites`:
     * - group: "sounds"
     * - className: "Sound"
     * - generator: "DefaultSound" -> [DefaultSoundsGenerator]
     */
    fun sounds(config: Action<AssetBatch>) = sounds.add(config)
    /**
     * The batch type of `shaders`:
     * - group: "shaders"
     * - className: "Shader"
     */
    val shaders = AssetBatchType(
        group = "shaders",
        className = "Shader",
    )
    /**
     * The batch type of `shaders`:
     * - group: "shaders"
     * - className: "Shader"
     */
    fun shaders(config: Action<AssetBatch>) = shaders.add(config)
    /**
     * The batch type of `shaders`:
     * - group: "bundles"
     * - className: "Bundle"
     */
    val bundles = AssetBatchType(
        group = "bundles",
        className = "Bundle",
    )
    /**
     * The batch type of `shaders`:
     * - group: "bundles"
     * - className: "Bundle"
     */
    fun bundles(config: Action<AssetBatch>) = bundles.add(config)
    /**
     * Create an [AssetBatchType] by the string given and return it
     * @receiver its [AssetBatchType.group]
     * @return the [AssetBatchType]
     */
    inline operator fun String.invoke(
        config: AssetBatchType.() -> Unit,
    ): AssetBatchType {
        val type = AssetBatchType(group = this)
        type.config()
        return type
    }
    /**
     * Create an [AssetBatchType] and return it
     * @return the [AssetBatchType]
     */
    fun BatchType(config: AssetBatchType.() -> Unit): AssetBatchType {
        val type = AssetBatchType()
        type.config()
        return type
    }
    /**
     * For configuring [assetsRoot]
     */
    inner class AssetRootSpec {
        /**
         * Set [assetsRoot] to [folder]
         */
        infix fun at(folder: File) {
            assetsRoot.set(folder)
        }
        /**
         * Set [assetsRoot] to [path]
         */
        infix fun at(path: String) {
            assetsRoot.set(File(path))
        }
    }

    inner class IconSpec {
        /**
         * Set [assetsRoot] to [file]
         */
        infix fun at(file: File) {
            _icon.set(file)
        }
        /**
         * Set [_icon] to [path]
         */
        infix fun at(path: String) {
            _icon.set(File(path))
        }
    }
    /**
     * An asset batch type.
     * It can be used for generating resource class.
     */
    inner class AssetBatchType(
        /**
         * The resource group.
         *
         * It will be used to name folder in `:jar` task output.
         *
         * It should follow [NameRule.Kebab]
         */
        var group: String = "",
        /**
         * Ihe name of generated class.
         *
         * It should follow [NameRule.Pascal]
         */
        var className: String = "",
        /**
         * The name of a generator,
         * which has been registered in [ResourceClassGeneratorRegistry] or [MindustryAssetsExtension.generators].
         */
        var generator: String = "",
        /**
         * The name rule of resources.
         * [NameRule.Kebab] as default
         */
        var nameRule: NameRule = NameRule.Kebab,
        /**
         * The special arguments for generating which will overwrite [MindustryAssetsExtension.args]
         */
        var args: MutableMap<String, String> = HashMap(),
    ) : Serializable {
        // For Groovy
        /**
         * Create and add a new [AssetBatch]
         */
        fun add(
            config: Action<AssetBatch>,
        ) {
            val newBatch = AssetBatch(this)
            config.execute(newBatch)
            batches.add(newBatch)
        }
        /**
         * Create and add a new [AssetBatch]
         */
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
    /**
     * @see NameRule.Pascal
     */
    @JvmField
    val Pascal = NameRule.Pascal
    /**
     * @see NameRule.Camel
     */
    @JvmField
    val Camel = NameRule.Camel
    /**
     * @see NameRule.Snake
     */
    @JvmField
    val Snake = NameRule.Snake
    /**
     * @see NameRule.AllCaps
     */
    @JvmField
    val AllCaps = NameRule.AllCaps
    /**
     * @see NameRule.Kebab
     */
    @JvmField
    val Kebab = NameRule.Kebab
    /**
     * @see NameRule.Domain
     */
    @JvmField
    val Domain = NameRule.Domain
}
/**
 * An asset batch.
 * It may be involved in generating resource class.
 */
data class AssetBatch(
    /**
     * The type of this, [AssetBatchType]
     */
    var type: AssetBatchType,
    /**
     * Whether to enable generating resource class.
     *
     * disable as default
     *
     * **Note:**
     * - If any [AssetBatch] of this [type] enabled generating, it'll register a [GenerateResourceClass] task, named `gen(AssetType)Class`.
     * - If no [AssetBatch] enabled this, [GenerateRClass] won't be registered.
     */
    var enableGenClass: Boolean = false,
    /**
     * The directory of resources.
     */
    var dir: File = File(""),
    /**
     * The root directory of resources.
     * It'll be used when you want to include a nested folder
     */
    var root: File = MindustryPlugin.DefaultEmptyFile,
    /**
     * What task the generating depends on.
     * It's useful to control the building chains.
     */
    var dependsOn: ArrayList<Any> = ArrayList(),
) : Serializable {
    fun dependsOn(task: Any) {
        dependsOn.add(task)
    }
    /**
     * Set [root] to [path]
     */
    fun rootAt(path: String) {
        root = File(path)
    }
    /**
     * Set [root] to [file]
     */
    fun rootAt(file: File) {
        root = file
    }
    /**
     * Do not generate resource class of this batch.
     * It's default.
     */
    val noGenClass: Unit
        get() {
            enableGenClass = false
        }
    /**
     * Generate resource class of this batch.
     */
    val genClass: Unit
        get() {
            enableGenClass = true
        }
}
/**
 * Resolve all batches for generating and copying assets
 */
fun List<AssetBatch>.resolveBatches():
    Map<AssetBatchType, List<AssetBatch>> =
    this.groupBy { it.type }
