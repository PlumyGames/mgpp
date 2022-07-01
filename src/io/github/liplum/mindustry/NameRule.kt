package io.github.liplum.mindustry

import org.gradle.configurationcache.extensions.capitalized

enum class NameRule {
    /**PascalNameRule*/
    Pascal {
        override fun split(raw: String): List<String> {
            if (raw.isEmpty()) return emptyList()
            if (raw.length == 1) return listOf(raw.lowercase())
            val buf = StringBuilder()
            val res = ArrayList<String>()
            for (c in raw) {
                if (c.isUpperCase()) {
                    if (buf.isNotEmpty()) {
                        val seg = buf.toString()
                        res.add(seg.lowercase())
                        buf.clear()
                        buf.append(c.lowercase())
                    } else {
                        buf.append(c.lowercase())
                    }
                } else
                    buf.append(c)
            }
            if (buf.isNotEmpty()) {
                val seg = buf.toString()
                res.add(seg.lowercase())
            }
            return res
        }

        override fun rename(segments: List<String>): String {
            if (segments.isEmpty()) return ""
            val sb = StringBuilder()
            for (seg in segments) {
                sb.append(seg.lowercase().capitalized())
            }
            return sb.toString()
        }
    },
    /**camelNameRule*/
    Camel {
        override fun split(raw: String): List<String> {
            if (raw.isEmpty()) return emptyList()
            if (raw.length == 1) return listOf(raw.lowercase())
            val buf = StringBuilder()
            val res = ArrayList<String>()
            for (c in raw) {
                if (c.isUpperCase()) {
                    if (buf.isNotEmpty()) {
                        val seg = buf.toString()
                        res.add(seg.lowercase())
                        buf.clear()
                        buf.append(c.lowercase())
                    } else {
                        buf.append(c.lowercase())
                    }
                } else
                    buf.append(c)
            }
            if (buf.isNotEmpty()) {
                val seg = buf.toString()
                res.add(seg.lowercase())
            }
            return res
        }

        override fun rename(segments: List<String>): String {
            if (segments.isEmpty()) return ""
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
        override fun split(raw: String): List<String> =
            raw.split("_")

        override fun rename(segments: List<String>): String {
            if (segments.isEmpty()) return ""
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
        override fun split(raw: String): List<String> =
            raw.split("_").map { it.lowercase() }

        override fun rename(segments: List<String>): String {
            if (segments.isEmpty()) return ""
            return segments.joinToString("_") { it.uppercase() }
        }
    },
    /**kebab-name-rule*/
    Kebab {
        override fun split(raw: String): List<String> =
            raw.split("-")

        override fun rename(segments: List<String>): String {
            if (segments.isEmpty()) return ""
            return segments.joinToString("-") { it.lowercase() }
        }
    },
    /**dot.name.rule*/
    Dot {
        override fun split(raw: String): List<String> =
            raw.split(".")

        override fun rename(segments: List<String>): String {
            return segments.joinToString(".") { it.lowercase() }
        }
    };

    abstract fun split(raw: String): List<String>
    abstract fun rename(segments: List<String>): String
    fun rename(segments: Array<String>): String = rename(segments.toList())
}