package io.github.liplum.dsl

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import java.io.File

typealias StringProp = Property<String>
typealias BoolProp = Property<Boolean>
typealias StringsProp = ListProperty<String>
typealias DirProp = DirectoryProperty
typealias FileProp = Property<File>