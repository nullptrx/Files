package io.github.nullptrx.files.example.navigation

import androidx.lifecycle.MediatorLiveData
import io.github.nullptrx.files.example.settings.Settings
import io.github.nullptrx.files.storage.StorageVolumeListLiveData

object NavigationItemListLiveData : MediatorLiveData<List<NavigationItem?>>() {
  init {
    // Initialize value before we have any active observer.
    loadValue()
    addSource(Settings.STORAGES) { loadValue() }
    addSource(StorageVolumeListLiveData) { loadValue() }
    addSource(StandardDirectoriesLiveData) { loadValue() }
    addSource(Settings.BOOKMARK_DIRECTORIES) { loadValue() }
  }

  private fun loadValue() {
    value = navigationItems
  }
}
