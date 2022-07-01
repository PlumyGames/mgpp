package io.github.liplum.dsl

enum class OS {
    Unknown, Windows, Linux, Mac
}

fun getOs(): OS = (System.getProperty("os.name").lowercase()).let {
    when {
        it.contains("windows") -> OS.Windows
        it.contains("mac os x") || it.contains("darwin") || it.contains("osx") -> OS.Mac
        it.contains("linux") -> OS.Linux
        else -> OS.Unknown
    }
}