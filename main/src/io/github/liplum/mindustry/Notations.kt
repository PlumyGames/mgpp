package io.github.liplum.mindustry

interface Notation {
    companion object {
        val latest = object : Notation {
            override fun toString() = "latest"
        }
        val latestRelease = object : Notation {
            override fun toString() = "latest-release"
        }
        val latestProperties = object : Notation {
            override fun toString() = "latest-properties"
        }
        val project = object : Notation {
            override fun toString() = "project"
        }
        val root = object : Notation {
            override fun toString() = "root"
        }
    }
}

object LatestNotation : Notation {
    override fun toString() = "latest"
}

object LatestReleaseNotation : Notation {
    override fun toString() = "latest-release"
}

object LocalPropertiesNotation : Notation {
    override fun toString() = "local-properties"
}

object ProjectNotation : Notation {
    override fun toString() = "project"
}

object RootNotation : Notation {
    override fun toString() = "root"
}