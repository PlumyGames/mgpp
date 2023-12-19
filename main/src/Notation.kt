package io.github.liplum.mindustry

interface Notation {
    companion object {
        val latest = object : Notation {
            override fun toString() = "latest"
        }
        val latestRelease = object : Notation {
            override fun toString() = "latest-release"
        }
    }
}
