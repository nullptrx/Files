package io.github.nullptrx.files.example.storage

import io.github.nullptrx.files.example.settings.Settings
import io.github.nullptrx.files.extension.removeFirst
import io.github.nullptrx.files.extension.valueCompat

object Storages {
  fun addOrReplace(storage: Storage) {
    val storages = Settings.STORAGES.valueCompat.toMutableList().apply {
      val index = indexOfFirst { it.id == storage.id }
      if (index != -1) {
        this[index] = storage
      } else {
        this += storage
      }
    }
    Settings.STORAGES.putValue(storages)
  }

  fun replace(storage: Storage) {
    val storages = Settings.STORAGES.valueCompat.toMutableList()
      .apply { this[indexOfFirst { it.id == storage.id }] = storage }
    Settings.STORAGES.putValue(storages)
  }

  fun move(fromPosition: Int, toPosition: Int) {
    val bookmarkDirectories = Settings.STORAGES.valueCompat.toMutableList()
      .apply { add(toPosition, removeAt(fromPosition)) }
    Settings.STORAGES.putValue(bookmarkDirectories)
  }

  fun remove(storage: Storage) {
    val bookmarkDirectories = Settings.STORAGES.valueCompat.toMutableList()
      .apply { removeFirst { it.id == storage.id } }
    Settings.STORAGES.putValue(bookmarkDirectories)
  }
}
