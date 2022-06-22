package io.github.nullptrx.files.example.ui.filelist.breadcrumb

import androidx.lifecycle.MediatorLiveData
import io.github.nullptrx.files.example.navigation.NavigationItemListLiveData
import io.github.nullptrx.files.example.navigation.NavigationRoot
import io.github.nullptrx.files.extension.valueCompat
import java8.nio.file.Path

object NavigationRootMapLiveData : MediatorLiveData<Map<Path, NavigationRoot>>() {
    init {
        // Initialize value before we have any active observer.
        loadValue()
        addSource(NavigationItemListLiveData) { loadValue() }
    }

    private fun loadValue() {
        value = NavigationItemListLiveData.valueCompat
            .mapNotNull { it as? NavigationRoot }
            .associateBy { it.path }
    }
}