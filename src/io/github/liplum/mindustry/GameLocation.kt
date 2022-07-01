package io.github.liplum.mindustry

import java.io.Serializable

data class GameLocation(
    var user: String = "",
    var repo: String = "",
    var version: String = "",
    var release: String = "",
) : Serializable {
}