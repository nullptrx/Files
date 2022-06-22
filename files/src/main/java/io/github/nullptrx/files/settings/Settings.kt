package io.github.nullptrx.files.settings

import android.os.Environment
import io.github.nullptrx.files.R
import io.github.nullptrx.files.provider.root.RootStrategy
import java8.nio.file.Path
import java8.nio.file.Paths

object Settings {

  val ROOT_STRATEGY: SettingLiveData<RootStrategy> =
    EnumSettingLiveData(
      R.string.pref_key_root_strategy, R.string.pref_default_value_root_strategy,
      RootStrategy::class.java
    )

  val FILE_LIST_DEFAULT_DIRECTORY: SettingLiveData<Path> =
    ParcelValueSettingLiveData(
      R.string.pref_key_file_list_default_directory,
      Paths.get(Environment.getExternalStorageDirectory().absolutePath)
    )

  val ARCHIVE_FILE_NAME_ENCODING: SettingLiveData<String> =
    StringSettingLiveData(
      R.string.pref_key_archive_file_name_encoding,
      R.string.pref_default_value_archive_file_name_encoding
    )

}