# Mindustry Extension

The `mindustry` extension is used to configure the `mod.[h]json`,
`dependency`, `run`, `deploy` and so on.

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
    projectType = Mod
    projectType = Plugin
    ```

=== "Kotlin"

    ``` kotlin
    projectType = Mod
    projectType = Plugin
    ```

It will affect the Dependency Resolution.

- Mod: Import all modules from `Mindustry` and `Arc`, including `core`, `desktop` and `server`.
- Plugin: Only import `core` and `server` module.

### Out of date time

It represents how much time the [`latest`](stuff.md#latest-notation) check will restart.
Unit: second
=== "Groovy"

    ``` groovy
    outOfDateTime = 60 * 60 // 60 minutes
    ```

=== "Kotlin"

    ``` kotlin
    outOfDateTime = 60 * 60 // 60 minutes
    ```

### Mod meta
It represents the `mod.[h]json` file of your mod/plugin.
MGPP will automatically search for it in order of the paths mentioned below:

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
    meta << ModMeta(
        name: 'test-name',
        displayName: 'Test Display Name',
        author: 'yourself',
        description: '...',
        subtitle: '...',
        version: '1.0',
        main: 'org.example.ExampleMod',
        minGameVersion: '135',
        repo: 'PlumyGame/mgpp',
        dependencies: ['plumy/mgpp'],
        hidden: false,
        java: true,
        keepOutline: false,
    )
    ```

=== "Kotlin"

    ``` kotlin
    meta += ModMeta(
        name = "test-name",
        displayName = "Test Display Name",
        author = "yourself",
        description = "...",
        subtitle = "...",
        version = "1.0",
        main = "org.example.ExampleMod",
        minGameVersion = "135",
        repo = "PlumyGame/mgpp",
        dependencies = listOf("plumy/mgpp"),
        hidden = false,
        java = true,
        keepOutline = false,
    )
    ```

Note: Some of them only exist in higher version of Mindustry.