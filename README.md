<div align="center">

# [Mindustry Gradle Plugin](https://plumygame.github.io/mgpp/)
[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.liplum.mgpp?color=02303a&label=Gradle%20Plugin&logo=Gradle&style=for-the-badge)](https://plugins.gradle.org/plugin/io.github.liplum.mgpp)
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
- Separate Working Space
- Manage your Assets
- Generate Resource Class
- Deploy on Android

Please check the [homepage](https://plumygame.github.io/mgpp/) to obtain more information.
## Usages

**Because this plugin was made in Kotlin, using Kotlin as your DSL is recommended.**

| Pages         | Instances                                                                                                                                                      |
|---------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Usages        | [Groovy DSL](https://plumygame.github.io/mgpp/groovy.html), [Kotlin DSL](https://plumygame.github.io/mgpp/kotlin.html)                                         |
| Samples       | [Groovy Sample](https://github.com/PlumyGame/mgpp/tree/master/TestProjectGroovy), [Kotlin Sample](https://github.com/PlumyGame/mgpp/tree/master/TestProjectKt) |
| Mod Templates | [MDT Mod Template](https://github.com/liplum/MdtModTemplate)                                                                                                   |

### Using the Plugin

Using the [plugins DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block):

```groovy
buildscript {
    repositories { maven { url "https://www.jitpack.io" } }
}
plugins {
    id "io.github.liplum.mgpp" version "1.0.3"
}
```

Using [legacy plugin application](https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application):

```groovy
buildscript {
    repositories {
        maven { url "https://plugins.gradle.org/m2/" }
        maven { url "https://www.jitpack.io" }
    }
    dependencies {
        classpath "io.github.liplum.mgpp:MindustryGradlePluginPlumy:1.0.3"
    }
}

apply plugin: "io.github.liplum.mgpp"
```

___

### Showcase

Configure the plugin in build.gradle.kts.

```kotlin
import plumy.mindustry.*

mindustry {
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
    meta += ModMeta(
        name = "your-mod-name",
        displayName = "Your mod",
        main = "org.example.FooMod",
        author = "yourself"
    )
    meta["version"] = "Kotlin 666"
    meta.minGameVersion = "136"
    mods {
        worksWith {
            add github "liplum/cyberio"
        }
    }
    deploy {
        baseName = "TestKotlinMod"
        version = "1.0.0"
    }
}
mindustryAssets {
    root at "$rootDir/assets"
}
```

## License

GNU General Public License v3.0 (GPL 3.0)