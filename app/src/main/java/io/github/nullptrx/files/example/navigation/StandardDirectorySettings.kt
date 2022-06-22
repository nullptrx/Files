package io.github.nullptrx.files.example.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StandardDirectorySettings(
  val id: String,
  val customTitle: String?,
  val isEnabled: Boolean
) : Parcelable
