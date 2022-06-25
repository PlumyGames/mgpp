package plumy.mindustry

import java.io.Serializable

data class GameLocation(
    var user: String = "",
    var repo: String = "",
    var version: String = "",
    var releaseName: String = "",
) : Serializable {
}