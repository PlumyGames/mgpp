@file:JvmMultifileClass
@file:JvmName("DslKt")

package io.github.liplum.dsl

import org.hjson.JsonType
import org.hjson.JsonValue
import kotlin.math.roundToInt

fun JsonValue.toList(): List<Any?> =
    this.convert() as? List<Any?> ?: emptyList()

@Suppress("UNCHECKED_CAST")
fun JsonValue.toMutableList(): MutableList<Any?> =
    this.convert() as? MutableList<Any?> ?: mutableListOf()
@Suppress("UNCHECKED_CAST")
fun JsonValue.toMap(): Map<String, Any?> =
    this.convert() as? Map<String, Any?> ?: emptyMap()

@Suppress("UNCHECKED_CAST")
fun JsonValue.toMutableMap(): MutableMap<String, Any?> =
    this.convert() as? MutableMap<String, Any?> ?: mutableMapOf()

fun JsonValue.convert(): Any? {
    return when (this.type) {
        JsonType.STRING -> this.asString()
        JsonType.NUMBER -> this.asDouble().toIntOrFloat()
        JsonType.OBJECT -> {
            val map = HashMap<String, Any?>()
            for (member in this.asObject()) {
                map[member.name] = member.value.convert()
            }
            map
        }

        JsonType.ARRAY -> {
            val array = ArrayList<Any?>()
            for (jv in this.asArray()) {
                array.add(jv.convert())
            }
            array
        }

        JsonType.BOOLEAN -> this.asBoolean()
        JsonType.NULL -> null
        JsonType.DSF -> this.asDsf()
        else -> {}
    }
}

fun Double.toIntOrFloat(): Any {
    val rint = this.roundToInt()
    if (rint.toDouble() == this) {
        return rint
    }
    return this.toFloat()
}