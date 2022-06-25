package plumy.mindustry

import java.io.File
import java.io.Serializable

interface IMod : Serializable {
    fun resolveFile(service: IModService): File
}

data class LocalMod(
    var modFile: File = File(""),
) : IMod {
    override fun resolveFile(service: IModService): File {
        return modFile
    }
}

interface IModService {
    val tempDir: File
}