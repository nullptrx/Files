package io.github.nullptrx.files.extension

import android.net.Uri
import io.github.nullptrx.files.app.contentResolver

fun Uri.takePersistablePermission(modeFlags: Int): Boolean =
  try {
    contentResolver.takePersistableUriPermission(this, modeFlags)
    true
  } catch (e: SecurityException) {
    e.printStackTrace()
    false
  }

fun Uri.releasePersistablePermission(modeFlags: Int): Boolean =
  try {
    contentResolver.releasePersistableUriPermission(this, modeFlags)
    true
  } catch (e: SecurityException) {
    e.printStackTrace()
    false
  }
