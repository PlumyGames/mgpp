### Keep other version
Whether to keep other versions when a new version is downloaded.

- `keepOthers`: To keep other versions when a new version is downloaded.
- `clearUp`: To clean all other versions when a new version is downloaded.

=== "Groovy"

    ``` groovy
    client/server {
        keepOthers
        clearUp
    }
    ```

=== "Kotlin"

    ``` kotlin
    client/server {
        keepOthers
        clearUp
    }
    ```

### Startup args
The extra startup arguments for Mindustry game.

=== "Groovy"

    ``` groovy
    client/server {
        args += [ '-gl2' ]
    }
    ```

=== "Kotlin"

    ``` kotlin
    client/server {
        args += listOf("-gl2") 
    }
    ```
