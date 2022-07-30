# Mods

You can add more mods working with your mod, such as a Json or Java from GitHub,
a local file, an url or even a gradle task.

The `mods{}` closure is inside of [`mindustry{}`](overview.md) closure hereinafter.

Related tasks: `resolveMods`

### Extra mods from task

This list contains task paths whose outputs will be copied into `dataDir/mods`.

Conventionally, when you apply `java` plugin,
mgpp will add `jar` task into the list automatically.
=== "Groovy"

    ``` groovy
    mods {
        extraModsFromTask += [ ':js:distZip' ]
    }
    ```

=== "Kotlin"

    ``` kotlin
    mods {
        extraModsFromTask += listOf( ":js:distZip" )
    }
    ```

### Works with

The `worksWith` closure let you add more mods working with yours.

#### Typed mods

MGPP will follow what Mindustry did to resolve those mods.

- Jvm Mod: mgpp will download it from its GitHub release page.
- JS/Json Mod: mgpp will download the zip of whole project on GitHub.

|  Mod Type   |              Expression              |
|:-----------:|:------------------------------------:|
|   Jvm Mod   | java, kotlin, groovy, scala, closure |
| JS/Json Mod |           json, hjson, js            |

As mgpp provided, you can use the syntax mentioned below to add a mod.
=== "Groovy"

    ``` groovy
    mods {
        worksWith {
            // pattern: expression + 'repo'
            java 'PlumyGame/mgpp'
        }
    }
    ```

=== "Kotlin"

    ``` kotlin
    mods {
        worksWith {
            // pattern: add expression + 'repo'
            add java "PlumyGame/mgpp"
        }
    }
    ```

For a Js/Json mod, you can specify its branch:

=== "Groovy"

    ``` groovy
    mods {
        worksWith {
            hjson 'PlumyGame/mgpp' branch 'v7'
        }
    }
    ```

=== "Kotlin"

    ``` kotlin
    mods {
        worksWith {
            add hjson "PlumyGame/mgpp" branch "v7"
        }
    }
    ```

#### Typeless mod

MGPP also allows to you add a mod without explicit type .

##### URL mod

MGPP will download it into the temporary directory of `resolveMods` task (hereinafter referred to as `temp`).

=== "Groovy"

    ``` groovy
    mods {
        worksWith {
            url 'https://example.org/ExapmleMod.jar'
        }
    }
    ```

=== "Kotlin"

    ``` kotlin
    mods {
        worksWith {
            add url "https://example.org/ExapmleMod.jar"
        }
    }
    ```

##### Local mod

MGPP will copy it from your local disk into `temp`

- local: the file specified here will be copied.
There is no effect if the file doesn't exist.
- localProperties: the value in `local.properties` file will be treated as a path for copying.
There is no effect if the key doesn't exist.
- folder: all files inside this folder will be copied.
The folder will be created if it doesn't exist.

=== "Groovy"

    ``` groovy
    mods {
        worksWith {
            local 'E:/mgpp/mod.jar'
            local new File('E:/mgpp/mod.jar')
            localProperties 'mods.what-mod-to-copy'
            folder 'E:/mod-repository'
            folder new File('E:/mod-repository')
        }
    }
    ```

=== "Kotlin"

    ``` kotlin
    mods {
        worksWith {
            add local "E:/mgpp/mod.jar"
            add local File("E:/mgpp/mod.jar")
            add localProperties "mods.what-mod-to-copy"
            add folder "E:/mod-repository"
            add folder File("E:/mod-repository")
        }
    }
    ```

##### General GitHub mod

MGPP will figure out the mod type the same as Mindustry does.

=== "Groovy"

    ``` groovy
    mods {
        worksWith {
            // In this case, it'll be treated as a Jvm mod
            github 'PlumyGame/mgpp'
        }
    }
    ```

=== "Kotlin"

    ``` kotlin
    mods {
        worksWith {
            // In this case, it'll be treated as a Jvm mod
            add github "PlumyGame/mgpp"
        }
    }
    ```

##### From Task
This way is almost the same as how you add an [extra mods from task](#extra-mods-from-task),
but you may benefit from its simple syntax.

=== "Groovy"

    ``` groovy
    mods {
        worksWith {
            // pattern: fromTask 'task path'
            fromTask ':js:distZip'
        }
    }
    ```

=== "Kotlin"

    ``` kotlin
    mods {
        worksWith {
            // pattern: add fromTask 'task path'
            add fromTask ":js:distZip"
        }
    }
    ```

#### Directly add
Anyway, if mgpp doesn't meet your need,
you can also directly add a mod,
which you can implement on your own [IMod](https://plumygame.github.io/mgppDoc/main/io.github.liplum.mindustry/-i-mod/index.html) class
, into [worksWith](https://plumygame.github.io/mgppDoc/main/io.github.liplum.mindustry/-mods-spec/index.html#-520366566%2FProperties%2F-140426848) set.

=== "Groovy"

    ``` groovy
    mods {
        worksWith {
            worksWith.add(new YourModClass(...))
        }
    }
    ```

=== "Kotlin"

    ``` kotlin
    mods {
        worksWith {
            worksWith.add(YourModClass(...))
        }
    }
    ```
