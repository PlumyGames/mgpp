<div align="center">

# [Mindustry Gradle Plugin](https://plumygame.github.io/mgpp/) [![Plumy](GFX/Discord.png)](https://discord.gg/3Hrep3WtUz)

[![Discord](https://img.shields.io/discord/937228972041842718?color=%23529b69&label=Discord&logo=Discord&style=for-the-badge)](https://discord.gg/3Hrep3WtUz)

A Mindustry gradle plugin, named Plumy.
[Homepage](https://plumygame.github.io/mgpp/)
___
</div>

## Features
- Manage the Dependencies
- Download the Game
- Debug your Mod
- Works with Other Mods
- Manage your Assets
- Generate Resource Class 
- Deploy on Android 

## Usages
**Because this plugin was made in Kotlin, using Kotlin as your DSL is recommended.**

Usages: [Groovy DSL](https://plumygame.github.io/mgpp/groovy.html), [Kotlin DSL](https://plumygame.github.io/mgpp/kotlin.html)

Samples: [Groovy Sample](https://github.com/PlumyGame/mgpp/tree/master/TestProjectGroovy), [Kotlin Sample](https://github.com/PlumyGame/mgpp/tree/master/TestProjectKt)

### Using the Plugin
```groovy
plugins {
    id "io.github.liplum.mgpp" version "1.0"
}
```
___
### Showcase
Configure the plugin in build.gradle.kts.
```kotlin
import plumy.mindustry.*
mindustry {
    projectType.set(Mod)
    dependency {
        mindustry on "v135"
        arc on "123fbf12b9"
    }
    client {
        mindustry official "v135"
    }
    server {
        mindustry official "v135"
    }
    assets {
        modMeta(
            name = "your-mod-name",
            displayName = "Your mod",
            main = "org.example.FooMod",
            author = "yourself"
        )
        meta["version"] = "Kotlin 666"
        meta.minGameVersion = "136"
    }
    mods {
        worksWith {
            add github "liplum/cyberio"
        }
    }
    deploy {
        outputJarName.set("TestKotlinMod")
        jarClassifier.set("1.0.0")
    }
}
mindustryAssets {
    root at "$rootDir/assets"
}
```
## License
GNU General Public License v3.0 (GPL 3.0)