@file:JvmMultifileClass
@file:JvmName("DslKt")

package io.github.liplum.dsl

import com.google.gson.Gson
import com.google.gson.GsonBuilder

internal
val gson: Gson = GsonBuilder().apply {
    setPrettyPrinting()
}.create()

internal
inline fun <reified T> Gson.fromJson(json: String): T =
    fromJson(json, T::class.java)