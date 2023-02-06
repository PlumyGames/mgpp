@file:Suppress("SpellCheckingInspection")

package io.github.liplum.mindustry

import groovy.json.JsonOutput
import io.github.liplum.dsl.toMutableMap
import org.hjson.JsonObject
import org.hjson.Stringify
import java.io.File
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * It represents the `mod.(h)json`.
 */
class ModMeta private constructor(
    val info: MutableMap<String, Any?>,
) : Serializable {

    constructor(
        name: String = default("name"),
        displayName: String = default("displayName"),
        author: String = default("author"),
        description: String = default("description"),
        /** since Mindustry v136 */
        subtitle: String = default("subtitle"),
        version: String = default("version"),
        main: String? = default("main"),
        minGameVersion: String = default("minGameVersion"),
        repo: String? = default("repo"),
        dependencies: List<String> = default("dependencies"),
        hidden: Boolean = default("hidden"),
        java: Boolean = default("java"),
        hideBrowser: Boolean = default("hideBrowser"),
        /** since Mindustry v136 */
        keepOutlines: Boolean = default("keepOutlines"),
        /** since Mindustry v138 */
        texturescale: Float = default("texturescale"),
        /** since Mindustry v138 */
        pregenerated: Boolean = default("pregenerated"),
    ) : this(
        mutableMapOf(
            "name" to name,
            "displayName" to displayName,
            "author" to author,
            "description" to description,
            "subtitle" to subtitle,
            "version" to version,
            "main" to main,
            "minGameVersion" to minGameVersion,
            "repo" to repo,
            "dependencies" to dependencies,
            "hidden" to hidden,
            "java" to java,
            "hideBrowser" to hideBrowser,
            "keepOutlines" to keepOutlines,
            "texturescale" to texturescale,
            "pregenerated" to pregenerated,
        )
    )
    /** For Kotlin */
    operator fun get(key: String): Any? = info[key]

    /** For Kotlin */
    operator fun set(key: String, meta: Any?) {
        info[key] = meta
    }

    /** For Groovy */
    fun getAt(key: String): Any? = info[key]

    /** For Groovy */
    fun putAt(key: String, meta: Any?) {
        info[key] = meta
    }

    /** For Groovy */
    fun propertyMissing(property: String): Any? = info[property]

    /** For Groovy */
    fun propertyMissing(property: String, value: Any?) {
        info[property] = value
    }

    /**
     * [ModMeta.toHjson]
     */
    override fun toString(): String {
        return toHjson()
    }

    companion object {
        @JvmStatic
        val defaultMeta = mapOf(
            "name" to "example",
            "displayName" to "Example Mod",
            "author" to "You",
            "description" to "It's an example mod.",
            "subtitle" to "I'm subtitle.",
            "version" to "1.0",
            "main" to null,
            "minGameVersion" to R.modMeta.defaultMinGameVersion,
            "repo" to null,
            "dependencies" to emptyList<String>(),
            "hidden" to false,
            "java" to true,
            "hideBrowser" to false,
            "keepOutlines" to false,
            "texturescale" to 1.0f,
            "pregenerated" to false,
        )
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <T> default(key: String): T =
            defaultMeta[key] as T
        @JvmStatic
        fun by(vararg metas: Map.Entry<String, Any?>) =
            ModMeta(HashMap(metas.associate { Pair(it.key, it.value) }).fillDefaultValue())
        @JvmStatic
        fun by(vararg metas: Pair<String, Any?>) =
            ModMeta(mutableMapOf(*metas).fillDefaultValue())
        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun fromHjson(hjson: String): ModMeta =
            ModMeta(JsonObject.readHjson(hjson).toMutableMap().fillDefaultValue())
        @JvmStatic
        fun fromHjson(file: File): ModMeta =
            runCatching {
                fromHjson(file.readText())
            }.getOrDefault(ModMeta())
        @JvmStatic
        @JvmOverloads
        fun ModMeta.toHjson(formatter: Stringify = Stringify.HJSON): String =
            JsonObject.readHjson(JsonOutput.toJson(info)).toString(formatter)
        @JvmStatic
        internal
        fun <T> T.fillDefaultValue(): T where T : MutableMap<String, Any?> {
            for ((dk, dv) in defaultMeta) {
                this.putIfAbsent(dk, dv)
            }
            return this
        }
    }
}

var ModMeta.name: String by meta()
var ModMeta.displayName: String by meta()
var ModMeta.author: String by meta()
var ModMeta.description: String by meta()
/** since Mindustry v136 */
var ModMeta.subtitle: String by meta()
var ModMeta.version: String by meta()
var ModMeta.main: String? by meta()
var ModMeta.minGameVersion: String by meta()
var ModMeta.repo: String? by meta()
var ModMeta.dependencies: List<String> by meta()
var ModMeta.hidden: Boolean by meta()
var ModMeta.java: Boolean by meta()
var ModMeta.hideBrowser: Boolean by meta()
/** since Mindustry v136 */
var ModMeta.keepOutlines: Boolean by meta()
/** since Mindustry v138 */
var ModMeta.texturescale: Float by meta()
/** since Mindustry v138 */
var ModMeta.pregenerated: Boolean by meta()
inline fun <reified T : Any?> meta(): ReadWriteProperty<ModMeta, T> =
    object : ReadWriteProperty<ModMeta, T> {
        override fun getValue(thisRef: ModMeta, property: KProperty<*>): T {
            val meta = thisRef.info[property.name] ?: return ModMeta.default(property.name)
            return if (meta is T)
                meta
            else if (T::class.java == String::class.java)
                meta.toString() as? T ?: ModMeta.default(property.name)
            else
                ModMeta.default(property.name)
        }

        override fun setValue(thisRef: ModMeta, property: KProperty<*>, value: T) {
            thisRef.info[property.name] = value
        }
    }