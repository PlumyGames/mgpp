package io.github.liplum.mindustry

import java.util.*

@Suppress("LeakingThis")
abstract class NameRule(
    val name: String,
) {
    init {
        allNameRules[name.lowercase()] = this
    }
    /**
     * Split the raw name from some parts by this name rule.
     *
     * E.g.:
     * > Following java reverse domain convention:
     * > `java.lang.String` -> `["java","lang","String"]`
     *
     * **Note:** it only guaranteed to split them by the rule.
     *
     * > Following kebab naming:
     * > `java-langString` -> `["java","langString"]`
     */
    abstract fun split(raw: String): List<String>
    /**
     * Compose the segments by this name rule.
     *
     * E.g.:
     * > Following java reverse domain convention:
     * > `["java","lang","String"]` -> `java.lang.string`
     *
     * **Note:** It will treat these letters by the rule.
     * > Following reverse domain naming:
     * > `["java","LangString"]` -> `java.langstring`
     */
    abstract fun rename(segments: List<String>): String

    companion object {
        @JvmStatic
        private val allNameRules = HashMap<String, NameRule>()
        @JvmStatic
        fun valueOf(name: String): NameRule? =
            allNameRules[name.lowercase()]
        /**
         * PascalNameRule
         *
         * Split -> It will split them by the capitalized letters.
         * `JaCaLangString` -> `["Ja","Va","Lang","String"]`
         *
         * Rename-> It will keep the first letter for each segment lowercase capitalized
         * `["JaVa","lang","string"]` -> JaVaLangString
         */
        @JvmStatic
        val Pascal = object : NameRule("pascal") {
            override fun split(raw: String): List<String> {
                if (raw.isEmpty()) return emptyList()
                if (raw.length == 1) return listOf(raw)
                val buf = StringBuilder()
                val res = ArrayList<String>()
                for (c in raw) {
                    if (c.isUpperCase() && buf.isNotEmpty()) {
                        res.add(buf.toString())
                        buf.clear()
                    }
                    buf.append(c)
                }
                // append the rest
                if (buf.isNotEmpty()) res.add(buf.toString())
                return res
            }

            override fun rename(segments: List<String>): String {
                if (segments.isEmpty()) return ""
                val sb = StringBuilder()
                for (seg in segments) {
                    sb.append(seg.uppercaseHead())
                }
                return sb.toString()
            }
        }
        /**
         * camelNameRule
         *
         * Split -> It will split them by the capitalized letters.
         * `javaLangStrIng` -> `["java","Lang","Str","Ing"]`
         *
         * Rename-> It will keep the first letter of first segment uppercase, and the following starting with lowercase
         * `["JaVa","lang","string"]` -> JaVaLangString
         */
        @JvmStatic
        val Camel = object : NameRule("camel") {
            override fun split(raw: String): List<String> {
                if (raw.isEmpty()) return emptyList()
                if (raw.length == 1) return listOf(raw)
                val buf = StringBuilder()
                val res = ArrayList<String>()
                for (c in raw) {
                    if (c.isUpperCase() && buf.isNotEmpty()) {
                        res.add(buf.toString())
                        buf.clear()
                    }
                    buf.append(c)
                }
                if (buf.isNotEmpty()) res.add(buf.toString())
                return res
            }

            override fun rename(segments: List<String>): String {
                if (segments.isEmpty()) return ""
                if (segments.size == 1) return segments[0].lowercase()
                // segments.size > 1
                val sb = StringBuilder()
                for ((i, seg) in segments.withIndex()) {
                    if (i == 0) sb.append(seg.lowercaseHead())
                    else sb.append(seg.uppercaseHead())
                }
                return sb.toString()
            }
        }
        /**
         * snake_name_rule
         *
         * Split -> It will split them by underline and convert them to lowercase.
         * `java_La_ngSt_rIng` -> `["java","la","ngst","ring"]`
         *
         * Rename-> It will keep all letters lowercase
         * `["JaVa","lang","String"]` -> "java_lang_string"
         */
        @JvmStatic
        val Snake = object : NameRule("snake") {
            override fun split(raw: String): List<String> =
                raw.split("_").map { it.lowercase() }

            override fun rename(segments: List<String>): String =
                if (segments.isEmpty()) ""
                else segments.joinToString("_") { it.lowercase() }
        }
        /**
         * ALL_CAPS_NAME_RULE
         *
         * Split -> It will split them by underline and convert them to lowercase.
         * `JAVA_LA_NgST_RING` -> `["java","la","ngst","ring"]`
         *
         * Rename-> It will keep the all letters uppercase
         * `["JaVa","lang","String"]` -> "JAVA_LANG_STRING"
         */
        @JvmStatic
        val AllCaps = object : NameRule("allcaps") {
            override fun split(raw: String): List<String> =
                raw.split("_").map { it.lowercase() }

            override fun rename(segments: List<String>): String =
                if (segments.isEmpty()) ""
                else segments.joinToString("_") { it.uppercase() }
        }
        /**
         * kebab-name-rule
         *
         * Split -> It will split them by hyphen and convert them to lowercase.
         * `JAvA-LA-NgST-RiNG` -> `["java","la","ngst","ring"]`
         *
         * Rename-> It will keep the all letters lowercase
         * `["JaVa","lang","String"]` -> "java-lang-string"
         */
        @JvmStatic
        val Kebab = object : NameRule("kebab") {
            override fun split(raw: String): List<String> =
                raw.split("-").map { it.lowercase() }

            override fun rename(segments: List<String>): String =
                if (segments.isEmpty()) ""
                else segments.joinToString("-") { it.lowercase() }
        }
        /**
         * domain.name.rule
         *
         * Split -> It will split them by dot.
         * `JAvA.LA.NgST.RiNG` -> `["JAvA","LA","NgST","RiNG"]`
         *
         * Rename-> It will only insert dot between two segments
         * `["JaVa","lang","String"]` -> "jaVa.lang.String"
         */
        @JvmStatic
        val Domain = object : NameRule("domain") {
            override fun split(raw: String): List<String> =
                raw.split(".")

            override fun rename(segments: List<String>): String {
                return segments.joinToString(".") { it }
            }
        }
    }
}
/**
 * `abcDef` -> `AbcDef`
 */
fun String.uppercaseHead(): String =
    when {
        isEmpty() -> ""
        else -> this[0].let { initial ->
            when {
                initial.isLowerCase() -> initial.uppercase(Locale.getDefault()) + substring(1)
                else -> toString()
            }
        }
    }
/**
 * `ABcDEf` -> `aBcDEf`
 */
fun String.lowercaseHead(): String =
    when {
        isEmpty() -> ""
        else -> this[0].let { initial ->
            when {
                initial.isUpperCase() -> initial.lowercase(Locale.getDefault()) + substring(1)
                else -> toString()
            }
        }
    }