package io.github.liplum.mindustry

import java.io.File

fun interface FileFilter {
    fun isAccept(file: File): Boolean
    class Set : FileFilter {
        var set = HashSet<FileFilter>()
        fun add(filter: FileFilter) {
            set.add(filter)
        }

        fun remove(filter: FileFilter): Boolean {
            return set.remove(filter)
        }

        operator fun plusAssign(filter: FileFilter) {
            set.add(filter)
        }

        operator fun minusAssign(filter: FileFilter) {
            set.remove(filter)
        }

        fun clear() {
            set.clear()
        }

        operator fun contains(filter: FileFilter): Boolean {
            return set.contains(filter)
        }

        override fun isAccept(file: File): Boolean {
            for (filter in set) {
                if (!filter.isAccept(file)) return false
            }
            return true
        }
    }

    companion object {
        @JvmStatic
        val always = FileFilter { true }
    }
}