# Installation
MGPP was published on [Gradle Plugin Portal](https://plugins.gradle.org/plugin/io.github.liplum.mgpp).

Please make sure to always upgrade MGPP to the latest version: [![Gradle Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.liplum.mgpp?color=02303a&label=&logo=Gradle&style=for-the-badge)](https://plugins.gradle.org/plugin/io.github.liplum.mgpp)

| Mindustry | MGPP Version |
|:---------:|:------------:|
|  ≤ 135.2  |   v 1.1.1    |
|   ≥ 136   |   v 1.2.0    |

___

## Gradle setup

- Using the [plugins DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block):
=== "Groovy"

    ``` groovy
    plugins {
        id "io.github.liplum.mgpp" version "<version>"
    }
    ```

=== "Kotlin"

    ``` kotlin
    plugins {
        id("io.github.liplum.mgpp") version "<version>"
    }
    ```
- Using [legacy plugin application](https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application):
=== "Groovy"

    ``` groovy
    buildscript {
        repositories {
            maven { url "https://plugins.gradle.org/m2/" }
        }
        dependencies {
            classpath "io.github.liplum.mgpp:MindustryGradlePluginPlumy:<version>"
        }
    }

    apply plugin: "io.github.liplum.mgpp"
    ```

=== "Kotlin"

    ``` kotlin
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

## Configuration
For ease of understanding, simplified examples are used here. Of course, it can still work for you.

=== "Groovy"

    ```groovy
    mindustry {
           dependency {
            mindustryMirror version: 'v141.2'
            arc version: 'v141.3'
        }
        client {
            official version: 'v141.3' 
        }
        server {
            official version: 'v141.3'
        }
    }
    mindustryAssets {
        rootAt "$projectDir/assets"
    }
    ```
=== "Kotlin"

    ```kotlin
    import io.github.liplum.mindustry.*
    mindustry {
        dependency {
            mindustry mirror "v141.2"
            arc on "v141.3"
        }
        client {
            mindustry official "v141.3"
        }
        server {
            mindustry official "v141.3"
        }
    }
    mindustryAssets {
        root at "$projectDir/assets"
    }
    ```

## Dependency setup
You can import the repositories where Mindustry was published by `mindustryRepo()`.

You can use `importMindustry()` to import the Mindustry dependencies you have configured above.

=== "Groovy"

    ``` groovy
    repositories {
        mavenCentral()
        mindustryRepo()
    }
    dependencies {
        importMindustry()
    }
    ```

=== "Kotlin"

    ``` kotlin
    import io.github.liplum.mindustry.*
    repositories {
        mavenCentral()
        mindustryRepo()
    }
    dependencies {
        importMindustry()
    }
    ```