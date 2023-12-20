package io.github.liplum.mindustry

import java.net.URL
import java.security.MessageDigest

fun URL.resolve4FileName(): String {
    val urlInBytes = MessageDigest
        .getInstance("SHA-1")
        .digest(this.toString().toByteArray())

    // Convert the byte array to a hexadecimal string
    val hexString = StringBuilder()
    for (byte in urlInBytes) {
        hexString.append(String.format("%02x", byte))
    }

    return hexString.toString()
}