# Mindustry Extension

The `mindustry` extension allows you to configure the `mod.[h]json`,
`dependency`, `run`, `deploy` and so on.

___

### Configurations

It has separated all configurations as mentioned below.

- [Dependency](dependency.md)
- [Client](client.md)
- [Server](server.md)
- [Mods](mods.md)
- [Run](run.md)
- [Deploy](deploy.md)

### Project type

MGPP supports 2 types of project: `Mod`, `Plugin`.
=== "Groovy"

    ``` groovy
    mindustry {
        projectType = Mod
        // OR
        projectType = Plugin
    }
    ```

=== "Kotlin"

    ``` kotlin
    import io.github.liplum.mindustry.*
    mindustry {
        projectType = Mod
        // OR
        projectType = Plugin
    }
    ```

It will affect the Dependency Resolution.

- Mod: Import all modules from `Mindustry` and `Arc`, including `core`, `desktop` and `server`.
- Plugin: Only import `core` and `server` module.

### Out of date time

It represents how much time the `latest` check will restart.
Unit: second
=== "Groovy"

    ``` groovy
    mindustry {
        outOfDateTime = 60 * 60 // 60 minutes
    }
    ```

=== "Kotlin"

    ``` kotlin
    mindustry {
        outOfDateTime = 60 * 60 // 60 minutes
    }
    ```
### Mod library
It's for anu library for Mindustry modding that benefits from MGPP.

If `isLib` is true, the task `jar` won't contain something included in a normal mod project.

- mod.hjson
- icon.png

=== "Groovy"

    ``` groovy
    mindustry {
        isLib = true
    }
    ```

=== "Kotlin"

    ``` kotlin
    mindustry {
        isLib = true
    }
    ```

### Mod meta
It represents the `mod.[h]json` file of your mod.
MGPP will automatically search for it by paths mentioned below orderly:

1. projectDir/mod.hjson
2. projectDir/mod.json
3. rootDir/mod.hjson
4. rootDir/mod.json

Every key in the mod meta corresponds to every key in `mod.[h]json` file.

You can append customized mod meta into the existed one,
however, it only affects the output jar file.
Thus, you could manipulate it internally in build script.

=== "Groovy"

    ``` groovy
    mindustry {
        meta << ModMeta(
            name: 'test-name',
            displayName: 'Test Display Name',
            author: 'yourself',
            description: '...',
            subtitle: '...',
            version: '1.0',
            main: 'org.example.ExampleMod',
            minGameVersion: '136',
            repo: 'PlumyGame/mgpp',
            dependencies: ['plumy/mgpp'],
            hidden: false,
            java: true,
            keepOutline: false,
        )
    }
    ```

=== "Kotlin"

    ``` kotlin
    mindustry {
        meta += ModMeta(
            name = "test-name",
            displayName = "Test Display Name",
            author = "yourself",
            description = "...",
            subtitle = "...",
            version = "1.0",
            main = "org.example.ExampleMod",
            minGameVersion = "136",
            repo = "PlumyGame/mgpp",
            dependencies = listOf("plumy/mgpp"),
            hidden = false,
            java = true,
            keepOutline = false,
        )
    }
    ```

Note: Some of them only exist in higher version of Mindustry.