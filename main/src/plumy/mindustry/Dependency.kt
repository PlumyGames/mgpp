package plumy.mindustry

import java.io.Serializable

interface IDependency : Serializable {
    fun isAvailable(module: String): Boolean = true
    fun resolve(module: String): String
}

inline fun IDependency.whenAvailable(
    module: String, func: (String) -> Unit,
) {
    if (isAvailable(module))
        func(resolve(module))
}

data class MirrorJitpackDependency(
    var fullName: String = "",
    var version: String = "",
) : IDependency {
    override fun isAvailable(module: String) =
        module == "server" || module == "core"

    override fun resolve(module: String) =
        "$fullName:$module:$version"
}

data class Dependency(
    var fullName: String = "",
    var version: String = "",
) : IDependency {
    override fun resolve(module: String) =
        "$fullName:$module:$version"
}