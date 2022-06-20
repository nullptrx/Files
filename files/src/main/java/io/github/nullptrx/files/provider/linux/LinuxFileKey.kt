package io.github.nullptrx.files.provider.linux

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class LinuxFileKey(
  private val deviceId: Long,
  private val inodeNumber: Long
) : Parcelable
