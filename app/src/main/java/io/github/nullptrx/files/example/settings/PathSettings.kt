package io.github.nullptrx.files.example.settings

import io.github.nullptrx.files.example.R
import io.github.nullptrx.files.example.ui.filelist.FileSortOptions
import io.github.nullptrx.files.settings.ParcelValueSettingLiveData
import io.github.nullptrx.files.settings.SettingLiveData
import java8.nio.file.Path

object PathSettings {
  private const val NAME_SUFFIX = "path"

  @Suppress("UNCHECKED_CAST")
  fun getFileListSortOptions(path: Path): SettingLiveData<FileSortOptions?> =
    ParcelValueSettingLiveData(
      NAME_SUFFIX, R.string.pref_key_file_list_sort_options, path.toString(), null
    )
}
