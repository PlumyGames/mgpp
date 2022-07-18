<div align="center">

# [Mindustry Gradle Plugin](https://plumygame.github.io/mgpp/)

[![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.liplum.mgpp?color=02303a&label=Gradle%20Plugin&logo=Gradle&style=for-the-badge)](https://plugins.gradle.org/plugin/io.github.liplum.mgpp)
[![Discord](https://img.shields.io/discord/937228972041842718?color=%23529b69&label=Discord&logo=Discord&style=for-the-badge)](https://discord.gg/3Hrep3WtUz)

A Mindustry gradle plugin, named Plumy.
___
</div>

## Features

- Manage the Dependencies
- Download the Game
- Debug your Mod
- Work with Other Mods
- Separate Working Space
- Manage your Assets
- Generate Resource Class
- Deploy on Android

Please check the [homepage](https://plumygame.github.io/mgpp/) to obtain more information.

## Supports

| Mindustry | MGPP Version |
|:---------:|:------------:|
|  ≤ 135.2  |   v 1.1.1    |
|   = 136   |   v 1.1.3    |


### How to Use

Please make sure to always upgrade MGPP to the latest
version [![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.liplum.mgpp?color=02303a&label=&logo=Gradle&style=for-the-badge)](https://plugins.gradle.org/plugin/io.github.liplum.mgpp)
.

<details open>
<summary>
    Groovy as DSL
</summary>

- Using the [plugins DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block):
    ```groovy
    plugins {
        id "io.github.liplum.mgpp" version "<version>"
    }
    ```
- Using [legacy plugin application](https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application):
    ```groovy
    buildscript {
        repositories {
            maven {
                url "https://plugins.gradle.org/m2/"
            }
        }
        dependencies {
            classpath "io.github.liplum.mgpp:MindustryGradlePluginPlumy:<version>"
        }
    }
    
    apply plugin: "io.github.liplum.mgpp"
    ```

</details>
<details>
<summary>
    Kotlin as DSL
</summary>

- Using the [plugins DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block):
    ```kotlin
    plugins {
      id("io.github.liplum.mgpp") version "<version>"
    }
    ```
- Using [legacy plugin application](https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application):
    ```kotlin
    buildscript {
        repositories {
            maven { url = uri("https://plugins.gradle.org/m2/") }
        }
        dependencies {
            classpath("io.github.liplum.mgpp:MindustryGradlePluginPlumy:<version>")
        }
    }
    
    apply(plugin = "io.github.liplum.mgpp")
    ```

</details>

## Documentation

|     Pages     |                                                                           Instances                                                                            |
|:-------------:|:--------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|    Usages     |                     [Groovy DSL](https://plumygame.github.io/mgpp/groovy.html), [Kotlin DSL](https://plumygame.github.io/mgpp/kotlin.html)                     |
|    Samples    | [Groovy Sample](https://github.com/PlumyGame/mgpp/tree/master/TestProjectGroovy), [Kotlin Sample](https://github.com/PlumyGame/mgpp/tree/master/TestProjectKt) |
|   Documents   |                          [Instruction](https://plumygame.github.io/mgpp/), [JavaDoc](https://plumygame.github.io/mgppDoc/index.html)                           |
| Mod Templates |                                                  [MDT Mod Template](https://github.com/liplum/MdtModTemplate)                                                  |


## License

GNU General Public License v3.0 (GPL 3.0)

## Icon

![preview](GFX/preview-small.png)
