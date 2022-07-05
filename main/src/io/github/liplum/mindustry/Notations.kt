package io.github.liplum.mindustry

interface INotation
object LatestNotation : INotation {
    override fun toString() = "latest"
}

object ProjectNotation : INotation {
    override fun toString() = "project"
}
object RootNotation : INotation {
    override fun toString() = "root"
}