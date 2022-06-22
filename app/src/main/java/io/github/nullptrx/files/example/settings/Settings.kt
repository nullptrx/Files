package io.github.nullptrx.files.example.settings

import io.github.nullptrx.files.example.R
import io.github.nullptrx.files.example.navigation.BookmarkDirectory
import io.github.nullptrx.files.example.navigation.StandardDirectorySettings
import io.github.nullptrx.files.example.storage.FileSystemRoot
import io.github.nullptrx.files.example.storage.PrimaryStorageVolume
import io.github.nullptrx.files.example.storage.Storage
import io.github.nullptrx.files.example.ui.filelist.FileSortOptions
import io.github.nullptrx.files.settings.ParcelValueSettingLiveData
import io.github.nullptrx.files.settings.SettingLiveData

object Settings {

  val STORAGES: SettingLiveData<List<Storage>> =
    ParcelValueSettingLiveData(
      R.string.pref_key_storages,
      // emptyList()，
      listOf(
        FileSystemRoot(null, true), PrimaryStorageVolume(null, true),
      )
    )

  val BOOKMARK_DIRECTORIES: SettingLiveData<List<BookmarkDirectory>> =
    ParcelValueSettingLiveData(R.string.pref_key_bookmark_directories, emptyList())


  val STANDARD_DIRECTORY_SETTINGS: SettingLiveData<List<StandardDirectorySettings>> =
    ParcelValueSettingLiveData(R.string.pref_key_standard_directory_settings, emptyList())

  val FILE_LIST_SORT_OPTIONS: SettingLiveData<FileSortOptions> =
    ParcelValueSettingLiveData(
      R.string.pref_key_file_list_sort_options,
      FileSortOptions(FileSortOptions.By.NAME, FileSortOptions.Order.ASCENDING, true)
    )
}