package plumy.dsl

import arc.util.serialization.JsonValue
import arc.util.serialization.JsonValue.ValueType

internal
fun Any?.atLeastEqualsOne(vararg possibilities: Any?): Boolean {
    for (possibility in possibilities) {
        if (this == possibilities) return true
    }
    return false
}
internal
operator fun StringBuilder.plusAssign(s: String) {
    this.append(s)
}
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
        ValueType.`object` -> {
            val map = HashMap<String, Any?>()
            var cur: JsonValue? = this.child
            while (cur != null) {
                map[cur.name] = cur.convert()
                cur = cur.next
            }
            map
        }
        ValueType.array -> {
            val list = ArrayList<Any?>()
            var cur: JsonValue? = this.child
            while (cur != null) {
                list.add(cur.convert())
                cur = cur.next
            }
            list
        }
        ValueType.stringValue -> asString()
        ValueType.doubleValue -> asDouble()
        ValueType.longValue -> asLong()
        ValueType.booleanValue -> asBoolean()
        ValueType.nullValue -> null
        else -> emptyMap<String, Any?>()
    }
}