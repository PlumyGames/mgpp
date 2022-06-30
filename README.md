<div align="center">

# Mindustry Gradle Plugin [![Plumy](GFX/Discord.png)](https://discord.gg/3Hrep3WtUz)

[![Discord](https://img.shields.io/discord/937228972041842718?color=%23529b69&label=Discord&logo=Discord&style=for-the-badge)](https://discord.gg/3Hrep3WtUz)

A Mindustry gradle plugin, named Plumy.
___
</div>

## Feature
**Manage** the **Dependencies:** Importing the dependencies of arc and Mindustry automatically with configuration.

**Download** the **Game:** Downloading any version of Mindustry easily.

**Debug** your **Mod:** Being able to debug your mod and Mindustry with an IDE, such as IntelliJ IDEA.

**Works with Other Mods:** Starting the game with your mod and others to test the compatibility or interaction.

**Manage** your **Assets**: Assets will be copied into the output jar file with configuration. 

**Generate Resource Class:** A class with references of assets to reduce boilerplate codes. 

**Deploy** on **Android:** Deploy your mod compatible to Mindustry on Android. *[Android SDK Required]*

## Usage
**Because this plugin was made in Kotlin, using Kotlin as your DSL is recommended.**

Samples: [Groovy sample](https://github.com/PlumyGame/mgpp/tree/master/TestProjectGroovy), [Kotlin sample](https://github.com/PlumyGame/mgpp/tree/master/TestProjectKt)

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
        projectType = Mod
           dependency {
            mindustry version: 'v135'
            // mirror version: 'd7312445a1'
            arc version: '123fbf12b9'
        }
        client {
            official version: 'v135' 
            // be version: '22714'
        }
        server {
            official version: 'v135'
            // be version: '22714'
        }
        assets {
            // This affects only the output jar. And as default,
            // it will read ModMeta from the mod.[h]json in the root directory.
            modMeta(
                name: 'your-mod-name',
                displayName: 'Your mod',
                main: 'org.example.FooMod',
                author: 'yourself'
            )
        }
        mods {
            worksWith {
                // You can import some mods to work with this mod in here.
                github 'liplum/cyberio'
            }
        }
        deploy {
            // You can configure the deployment task here
        }
    }
    ```
    ```kotlin
    // Kotlin as DSL
    import plumy.mindustry.*
    mindustry {
        projectType.set(Mod)
        dependency {
            mindustry on "v135"
            // mindustry mirror "d7312445a1"
            arc on "123fbf12b9"
        }
        client {
            mindustry official "v135"
            // mindustry be "22728"
        }
        server {
            mindustry official "v135"
            // mindustry be "22728"
        }
        assets {
            // This affects only the output jar. And as default,
            // it will read ModMeta from the mod.[h]json in the root directory.
            modMeta(
                name = "your-mod-name",
                displayName = "Your mod",
                main = "org.example.FooMod",
                author = "yourself"
            )
        }
        mods {
            // You can import some mods to work with this mod in here.
            worksWith {
                add github "liplum/cyberio"
            }
        }
        deploy {
            // You can configure the deployment task here
        }
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
   
## License
GNU General Public License v3.0 (GPL 3.0)