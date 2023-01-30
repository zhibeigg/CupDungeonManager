package com.github.cupdungeonmanager.limit.util

import taboolib.common.platform.function.getDataFolder
import java.io.File


fun files(path: String, callback: (File) -> Unit) {
    val file = File(getDataFolder(), path)
    getFiles(file).forEach { callback(it) }
}

fun getFiles(file: File): List<File> {
    val listOf = mutableListOf<File>()
    when (file.isDirectory) {
        true -> listOf += file.listFiles().flatMap { getFiles(it) }
        false -> {
            if (file.name.endsWith(".yml")) {
                listOf += file
            }
        }
    }
    return listOf
}
