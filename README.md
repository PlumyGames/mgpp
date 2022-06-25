<div align="center">

# Mindustry Gradle Plugin [![Plumy](GFX/Discord.png)](https://discord.gg/3Hrep3WtUz)

[![Discord](https://img.shields.io/discord/937228972041842718?color=%23529b69&label=Discord&logo=Discord&style=for-the-badge)](https://discord.gg/3Hrep3WtUz)

A Mindustry gradle plugin, named Plumy.
___
</div>

## Usage
**Because this plugin was made in Kotlin, using Kotlin as your DSL is recommended.**
1. Import the plugin to the build script:

    ```groovy
    // Groovy as DSL
    plugins{
        // Not yet release
    }
    ```
    ```kotlin
    // Kotlin as DSL
    plugins{
        // Not yet release
    }
    ```
2. Configure the plugin

    ```groovy
   // Groovy as DSL
    mindustry {
        projectType.set ProjectType.Mod
        assets {
            // This affects only the output jar. And as default,
            // it will read ModMeta from the mod.[h]json in the root directory.
            modMeta.set ModMeta(
                name: "your-mod-name",
                displayName: "Your mod",
                main: "org.example.FooMod",
                author: "yourself"
            )
        }
        mods {
            // You can import some mods to work with this mod in here.
        }
        deploy {
            // You can configure the android sdk or d8 in here
            d8Path.set "d8"
        }
    }
   // Use the gradle task temporary directory as the data directory of Mindustry,
   // you can isolate each different project on your computer.
    tasks.named("runClient") {
        dataOnTemporary()
    }
    ```
    ```kotlin
    // Kotlin as DSL
    import plumy.mindustry.mindustry
    mindustry {
        projectType.set(ProjectType.Mod)
        assets {
            // This affects only the output jar. And as default,
            // it will read ModMeta from the mod.[h]json in the root directory.
            modMeta.set(ModMeta(
                name = "your-mod-name",
                displayName = "Your mod",
                main = "org.example.FooMod",
                author = "yourself"
            ))
        }
        mods {
            // You can import some mods to work with this mod in here.
        }
        deploy {
            // You can configure the android sdk or d8 in here
            d8Path.set("d8")
        }
    }
    // Use the gradle task temporary directory as the data directory of Mindustry,
    // you can isolate each different project on your computer.
    tasks.withType<RunMindustryTask> {
        dataOnTemporary()
    }
    ```
3. Import the dependencies of Mindustry.
    ```groovy
    // Groovy as DSL
    repositories {
        mavenCentral()
        use(plumy.mindustry.BuildScript) {
            mindustryRepo()
        }
    }
    dependencies {
        use(plumy.mindustry.BuildScript) {
            importMindustry()
        }
    }
    ```
    ```kotlin
    // Kotlin as DSL
    import plumy.mindustry.mindustryRepo
    repositories {
        mavenCentral()
        mindustryRepo()
    }
    dependencies {
        importMindustry()
    }
    ```
4. Run the task
    In the group, mindustry, there are many tasks for building and debugging mods.
    Also, you can find them in your IDE.
    ``` shell
   # runClient or runServer: to download the game on GitHub and run it.
   .\gradlew runClient
   
   # deploy: if the android sdk is set properly,
   # a jar compitable on both Desktop and Android will be outputted in ./build/tmp/deploy folder. 
   .\gradew deploy
    ```