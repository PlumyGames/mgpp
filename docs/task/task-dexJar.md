# dexJar

##### Attributes
- **TYPE** `io.github.liplum.mindustry.task.DexJar`

### DexJarOptions
- `minApi`: `14` as default


=== "Groovy"

    ``` groovy
    dexJar {
        options.minApi = '26'
        // OR
        options {
            minApi = '26'
        }
    }
    ```

=== "Kotlin"

    ``` kotlin
    import io.github.liplum.mindustry.*
    tasks.dexJar {
        options.minApi = "26"
        // OR
        options {
            minApi = "26"
        }
    }
    ```
