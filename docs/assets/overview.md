# Mindustry Assets

The `mindustryAssets` extension allows you to configure assets, 
including `sprites`, `bundles`, `shaders`, `sounds` and anything
you want to add into your mod.

___

Assume your project has this structure:
```
YourMod/
├─ assets/
│  ├─ bundles/
│  ├─ sprites/
├─ src/
│  ├─ example/
│  │  ├─ ExampleMod.java
├─ build.gradle
├─ gradle.properties

```

### Simple usage

#### Assets
In most cases, you can just simply copy all files
recursively from the `assets root folder` into the `jar` task with this syntax:

=== "Groovy"

    ``` groovy
    mindustryAssets {
        rootAt "$projectDir/assets"
    }
    ```

=== "Kotlin"

    ``` kotlin
    import io.github.liplum.mindustry.*
    mindustryAssets {
        root at "$projectDir/assets"
    }
    ```

#### Icon
MGPP will automatically search for it in order of the paths mentioned below:

1. projectDir/icon.png
2. rootDir/icon.png

Also, you can configure it to any file/path your want.

=== "Groovy"

    ``` groovy
    mindustryAssets {
        iconAt "$rootDir/icon.png"
    }
    ```

=== "Kotlin"

    ``` kotlin
    import io.github.liplum.mindustry.*
    mindustryAssets {
        icon at "$rootDir/icon.png"
    }
    ```


### Advanced usage

MGPP provides `AssetBatchType` and `AssetBatch` for generating resource class,
named `R.java` conventionally.

Due to rare use cases, 
please check [its specific page](advanced.md) for more information.