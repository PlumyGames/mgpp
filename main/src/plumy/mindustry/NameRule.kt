package plumy.mindustry

import org.gradle.configurationcache.extensions.capitalized

enum class NameRule {
    /**PascalNameRule*/
    Pascal {
        override fun rename(segments: List<String>): String {
            val sb = StringBuilder()
            for (seg in segments) {
                sb.append(seg.lowercase().capitalized())
            }
            return sb.toString()
        }
    },
    /**camelNameRule*/
    Camel {
        override fun rename(segments: List<String>): String {
            val sb = StringBuilder()
            for ((i, seg) in segments.withIndex()) {
                if (i == 0) {
                    sb.append(seg.lowercase())
                } else {
                    sb.append(seg.lowercase().capitalized())
                }
            }
            return sb.toString()
        }
    },
    /**snake_name_rule*/
    Snake {
        override fun rename(segments: List<String>): String {
            val sb = StringBuilder()
            for ((i, seg) in segments.withIndex()) {
                sb.append(seg.lowercase())
                if (i < segments.size - 1) {
                    sb.append('_')
                }
            }
            return sb.toString()
        }
    },
    /**ALL_CAPS_NAME_RULE*/
    AllCaps {
        override fun rename(segments: List<String>): String {
            val sb = StringBuilder()
            for ((i, seg) in segments.withIndex()) {
                sb.append(seg.uppercase())
                if (i < segments.size - 1) {
                    sb.append('_')
                }
            }
            return sb.toString()
        }
    };

    abstract fun rename(segments: List<String>): String
    fun rename(segments: Array<String>): String = rename(segments.toList())
}