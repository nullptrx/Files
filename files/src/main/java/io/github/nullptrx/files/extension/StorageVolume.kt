package io.github.nullptrx.files.extension

import android.os.storage.StorageVolume
import io.github.nullptrx.files.compat.directoryCompat

val StorageVolume.isMounted: Boolean
  get() = directoryCompat != null