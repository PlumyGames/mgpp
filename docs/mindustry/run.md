# Run
The `run{}` closure is inside of [`mindustry{}`](overview.md) closure hereinafter.

### Data Directory
Mindustry will store saves and settings in the data directory.

Mindustry client will check an environment variable, `MINDUSTRY_DATA_DIR`, first.  

#### Default Data Directory
If the data directory is invalid or not yet set,
Mindustry will create the default data directory: 

=== "Linux"

    $HOME/.local/share/Mindustry/

=== "Windows"

    %AppData%/Mindustry/

=== "MacOS"

    $HOME/Library/Application Support/Mindustry/

#### Customize Data Directory
MGPP allows you to customize the location of data directory:

- A path: MGPP will treat it as a directory.
- useDefaultDataDir: MGPP will set it to [the default](#default-data-directory).
- useTempDataDir: MGPP will set it to the temporary directory of task:
    - in case of `runClient`, it's `buildDir/tmp/runClient/data`
    - in case of `runServer`, it's `buildDir/tmp/runServer/data`
- useEnvDataDir: MGPP will check the environment variable, `MINDUSTRY_DATA_DIR`.

=== "Groovy"

    ``` groovy
    run {
        dataDir = 'E:\MindustryData'
        useDefaultDataDir
        useTempDataDir
        useEnvDataDir
    }
    ```

=== "Kotlin"

    ``` kotlin
    run {
        dataDir = 'E:\MindustryData'
        useDefaultDataDir
        useTempDataDir
        useEnvDataDir
    }
    ```

MGPP will apply the `useTempDataDir` as default.

### Forcibly Clear

Whether to delete all mods other than any from [ModsSpec.worksWith](mods.md#works-with) 
in the [data directory](#data-directory),
ensure you have backed up your mods if it's on.

- clearOtherMods: To clear all things in the [data directory](#data-directory).
- keepOtherMods: To keep anything in the [data directory](#data-directory).

=== "Groovy"

    ``` groovy
    run {
        clearOtherMods
        // OR
        keepOtherMods
    }
    ```

=== "Kotlin"

    ``` kotlin
    run {
        clearOtherMods
        // OR
        keepOtherMods
    }
    ```

MGPP will apply the `keepOtherMods` as default.
