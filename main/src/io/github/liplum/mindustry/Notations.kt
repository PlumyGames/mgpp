package io.github.liplum.mindustry

interface INotation
object LatestNotation : INotation {
    override fun toString() = "latest"
}
object LatestReleaseNotation : INotation {
    override fun toString() = "latest-release"
}

object LocalPropertiesNotation:INotation{
    override fun toString() = "local-properties"
}

object ProjectNotation : INotation {
    override fun toString() = "project"
}
object RootNotation : INotation {
    override fun toString() = "root"
}