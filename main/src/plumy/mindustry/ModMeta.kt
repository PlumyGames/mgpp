package plumy.mindustry

import arc.util.serialization.Json
import arc.util.serialization.Jval
import groovy.json.JsonOutput
import org.hjson.JsonObject
import org.hjson.Stringify
import java.io.File
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

typealias MetaConfig = HashMap<String, Any>

data class ModMeta(
    val info: MetaConfig = HashMap(),
) : Serializable {
    constructor(info: Map<String, Any>) : this(HashMap(info).setDefaultValue())
    constructor(
        name: String = default("name"),
        displayName: String = default("displayName"),
        author: String = default("author"),
        description: String = default("description"),
        subtitle: String = default("subtitle"),
        version: String = default("version"),
        main: String = default("main"),
        minGameVersion: String = default("minGameVersion"),
        repo: String = default("repo"),
        dependencies: List<String> = default("dependencies"),
        hidden: Boolean = default("hidden"),
        java: Boolean = default("java"),
        hideBrowser: Boolean = default("hideBrowser"),
    ) : this(
        HashMap(
            mapOf(
                "name" to name,
                "displayName" to displayName,
                "author" to author,
                "description" to description,
                /** since Mindustry v136 */
                "subtitle" to subtitle,
                "version" to version,
                "main" to main,
                "minGameVersion" to minGameVersion,
                "repo" to repo,
                "dependencies" to dependencies,
                "hidden" to hidden,
                "java" to java,
                "hideBrowser" to hideBrowser,
            )
        )
    )
    // For Kotlin
    operator fun get(key: String): Any? =
        info[key]
    // For Kotlin
    operator fun set(key: String, meta: Any) {
        info[key] = meta
    }
    // For Groovy
    fun getAt(key: String): Any? =
        info[key]
    // For Groovy
    fun putAt(key: String, meta: Any) {
        info[key] = meta
    }
    // For Groovy
    fun propertyMissing(property: String): Any? =
        info[property]
    // For Groovy
    fun propertyMissing(property: String, value: Any) {
        info[property] = value
    }

    operator fun plusAssign(addition: ModMeta) {
        for ((k, newV) in addition.info) {
            val default = defaultMeta[k]
            if (default != null) {
                if (newV != default) {
                    this.info[k] = newV
                }
            } else {
                this.info[k] = newV
            }
        }
    }

    override fun toString(): String {
        return toHjson()
    }

    companion object {
        val defaultMeta = mapOf(
            "name" to "name",
            "displayName" to "",
            "author" to "",
            "description" to "",
            "subtitle" to "",
            "version" to "1.0",
            "main" to "",
            "minGameVersion" to Meta.DefaultMinGameVersion,
            "repo" to "",
            "dependencies" to emptyList<String>(),
            "hidden" to false,
            "java" to true,
            "hideBrowser" to false,
        )
        @Suppress("UNCHECKED_CAST")
        fun <T> default(key: String): T =
            defaultMeta[key] as T
        @JvmStatic
        fun by(vararg metas: Map.Entry<String, Any>) =
            ModMeta(metas.associate { Pair(it.key, it.value) })
        @JvmStatic
        fun by(vararg metas: Pair<String, Any>) =
            ModMeta(metas.toMap())
        internal
        val json = Json()
        @JvmStatic
        fun fromHjson(hjson: String): ModMeta =
            json.fromJson(ModMeta::class.java, Jval.read(hjson).toString(Jval.Jformat.plain))
        @JvmStatic
        fun fromHjson(file: File): ModMeta =
            runCatching {
                json.fromJson(ModMeta::class.java, Jval.read(file.readText()).toString(Jval.Jformat.plain))
            }.getOrDefault(ModMeta())
        @JvmStatic
        @JvmOverloads
        fun ModMeta.toHjson(formatter: Stringify = Stringify.HJSON): String =
            JsonObject.readHjson(JsonOutput.toJson(info)).toString(formatter)

        fun MetaConfig.setDefaultValue(): MetaConfig {
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
var ModMeta.main: String by meta()
var ModMeta.minGameVersion: String by meta()
var ModMeta.repo: String by meta()
var ModMeta.dependencies: List<String> by meta()
var ModMeta.hidden: Boolean by meta()
var ModMeta.java: Boolean by meta()
var ModMeta.hideBrowser: Boolean by meta()
inline fun <reified T : Any> meta(): ReadWriteProperty<ModMeta, T> =
    object : ReadWriteProperty<ModMeta, T> {
        override fun getValue(thisRef: ModMeta, property: KProperty<*>): T =
            thisRef.info[property.name] as? T ?: ModMeta.default(property.name)

        override fun setValue(thisRef: ModMeta, property: KProperty<*>, value: T) {
            thisRef.info[property.name] = value as Any
        }
    }
@Suppress("UNCHECKED_CAST")
internal
operator fun <T> Map<String, Any>.get(key: String, default: T): T {
    return this.getOrDefault(key, default) as T
}