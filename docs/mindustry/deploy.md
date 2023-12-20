# Deploy

MGPP provides two useful tasks for deployment on Android, `dexJar` and `deploy`,
but they only work when `java` plugin is enabled.

The `deploy{}` closure is inside of [`mindustry{}`](overview.md) closure hereinafter.

Related tasks: `jar`, `dexJar`, `deploy` 

___

### Deployment Jar
The `deploy` task will output the deployment jar, which contains `classes.dex` and your stuffs

#### Destination Directory
By default, `deploy` task outputs into its temporary directory, `buildDir/tmp/deploy`.

You can customize its destination to fit your CI, vice versa.

#### Name

The name of deployment jar is in the pattern of `baseName-version-classifier.jar`

Default values:

- baseName: `name` in your [mod meta](overview.md#mod-meta)
- version: `version` in your [mod meta](overview.md#mod-meta)
- classifier: empty

=== "Groovy"

    ``` groovy
    deploy {
        baseName = 'ExampleMod'
        version = '1.0'
        classifer = ''        
    }
    ```

=== "Kotlin"

    ``` kotlin
    deploy {
        baseName = "ExampleMod"
        version = "1.0"
        classifer = ""
    }
    ```

### Enable fat jar
Whether to make a `fat jar`, which contains all dependencies from classpath, in the `jar` task.

It's useful when you utilize other library or make a Kotlin mod.

- `fatJar`: To enable fat jar.
- `noFatJar`: To disable fat jar.

=== "Groovy"

    ``` groovy
    deploy {
        fatJar
        // OR
        noFatJar
    }
    ```

=== "Kotlin"

    ``` kotlin
    deploy {
        fatJar
        // OR
        noFatJar
    }
    ```
MGPP will apply `fatJar` by default.

### Android SDK

You can configure the location of Android SDK,
but mgpp doesn't recommend you to do that.

MGPP will automatically search for Android SDK in order of the environment variables mentioned below:

- `ANDROID_HOME`
- `ANDROID_SDK_ROOT`

=== "Groovy"

    ``` groovy
    deploy {
        androidSdkRoot = 'D:/AnroidSDK'
    }
    ```

=== "Kotlin"

    ``` kotlin
    deploy {
        androidSdkRoot = "D:/AnroidSDK"
    }
    ```