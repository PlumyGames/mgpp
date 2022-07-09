package io.github.liplum.dsl

import com.google.gson.Gson
import com.google.gson.GsonBuilder

val gson: Gson = GsonBuilder().apply {
    setPrettyPrinting()
}.create()

inline fun <reified T> Gson.fromJson(json: String): T =
    fromJson(json, T::class.java)