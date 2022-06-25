package plumy.mindustry

import arc.util.serialization.Json
import arc.util.serialization.Jval
import groovy.json.JsonOutput
import org.hjson.JsonObject
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
        name: String = "",
        displayName: String = "",
        author: String = "",
        description: String = "",
        subtitle: String = "",
        version: String = "1.0",
        main: String = "",
        minGameVersion: String = Meta.DefaultMinGameVersion,
        repo: String = "",
        dependencies: List<String> = emptyList(),
        hidden: Boolean = false,
        java: Boolean = true,
        hideBrowser: Boolean = true,
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

    companion object {
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
        fun ModMeta.toHjson(): String =
            JsonObject.readHjson(JsonOutput.toJson(info)).toString()

        fun MetaConfig.setDefaultValue(): MetaConfig {
            this.putIfAbsent("name", "")
            this.putIfAbsent("displayName", "")
            this.putIfAbsent("author", "")
            this.putIfAbsent("description", "")
            this.putIfAbsent("subtitle", "")
            this.putIfAbsent("version", "1.0")
            this.putIfAbsent("main", "")
            this.putIfAbsent("minGameVersion", Meta.DefaultMinGameVersion)
            this.putIfAbsent("repo", "")
            this.putIfAbsent("dependencies", emptyList<String>())
            this.putIfAbsent("hidden", false)
            this.putIfAbsent("java", true)
            this.putIfAbsent("hideBrowser", false)
            return this
        }
    }
}

var ModMeta.name: String by meta("")
var ModMeta.displayName: String by meta("")
var ModMeta.author: String by meta("")
var ModMeta.description: String by meta("")
/** since Mindustry v136 */
var ModMeta.subtitle: String by meta("")
var ModMeta.version: String by meta("1.0")
var ModMeta.main: String by meta("")
var ModMeta.minGameVersion: String by meta(Meta.DefaultMinGameVersion)
var ModMeta.repo: String by meta("")
var ModMeta.dependencies: List<String> by meta(emptyList())
var ModMeta.hidden: Boolean by meta(false)
var ModMeta.java: Boolean by meta(true)
var ModMeta.hideBrowser: Boolean by meta(false)
inline fun <reified T : Any> meta(default: T): ReadWriteProperty<ModMeta, T> =
    object : ReadWriteProperty<ModMeta, T> {
        override fun getValue(thisRef: ModMeta, property: KProperty<*>): T =
            thisRef.info[property.name] as? T ?: default

        override fun setValue(thisRef: ModMeta, property: KProperty<*>, value: T) {
            thisRef.info[property.name] = value as Any
        }
    }
@Suppress("UNCHECKED_CAST")
internal
operator fun <T> Map<String, Any>.get(key: String, default: T): T {
    return this.getOrDefault(key, default) as T
}