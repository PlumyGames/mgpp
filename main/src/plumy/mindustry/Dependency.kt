package plumy.mindustry

data class Dependency(
    var fullName: String = "",
    var version: String = "",
) : java.io.Serializable {
    fun resolve(module: String) =
        "$fullName:$module:$version"
}