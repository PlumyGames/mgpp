@file:Suppress("SpellCheckingInspection")

import io.github.liplum.mindustry.*

plugins {
    id("io.github.liplum.mgpp")
}

version = "1.0"
group = "net.liplum"

mindustry {
    modMeta {
        name = "js"
        displayName = "Js Mod"
        minGameVersion = "136"
    }
}
runMindustry {
    addClient {
        official(version = "v146")
    }
}
mindustryAssets {
    assets.from("$projectDir/assets")
}