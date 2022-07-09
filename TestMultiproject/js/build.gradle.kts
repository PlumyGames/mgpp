@file:Suppress("SpellCheckingInspection")

import io.github.liplum.mindustry.*

plugins {
    id("io.github.liplum.mgpp")
}

version = "1.0"
group = "net.liplum"

mindustry {
    client {
        mindustry be latest
    }
    meta += ModMeta(
        name = "js",
        displayName = "Js Mod",
        minGameVersion = "136",
    )
}
mindustryAssets {
    root at "$projectDir/assets"
}