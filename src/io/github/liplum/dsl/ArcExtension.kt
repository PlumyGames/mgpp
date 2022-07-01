package io.github.liplum.dsl

import arc.util.serialization.JsonValue

@Suppress("UNCHECKED_CAST")
fun JsonValue.toMap(): Map<String, Any?> {
    return this.convert() as? Map<String, Any?> ?: emptyMap()
}
@Suppress("UNCHECKED_CAST")
fun JsonValue.toList(): List<Any?> {
    return this.convert() as? List<Any?> ?: emptyList()
}

fun JsonValue.convert(): Any? {
    return when (type()) {
        JsonValue.ValueType.`object` -> {
            val map = HashMap<String, Any?>()
            var cur: JsonValue? = this.child
            while (cur != null) {
                map[cur.name] = cur.convert()
                cur = cur.next
            }
            map
        }
        JsonValue.ValueType.array -> {
            val list = ArrayList<Any?>()
            var cur: JsonValue? = this.child
            while (cur != null) {
                list.add(cur.convert())
                cur = cur.next
            }
            list
        }
        JsonValue.ValueType.stringValue -> asString()
        JsonValue.ValueType.doubleValue -> asDouble()
        JsonValue.ValueType.longValue -> asLong()
        JsonValue.ValueType.booleanValue -> asBoolean()
        JsonValue.ValueType.nullValue -> null
        else -> emptyMap<String, Any?>()
    }
}