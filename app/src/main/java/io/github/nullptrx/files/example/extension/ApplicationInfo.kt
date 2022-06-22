package io.github.nullptrx.files.example.extension

import android.content.pm.ApplicationInfo
import android.os.Build
import io.github.nullptrx.files.hiddenapi.RestrictedHiddenApi
import io.github.nullptrx.files.util.lazyReflectedField

@RestrictedHiddenApi
private val versionCodeField by lazyReflectedField(ApplicationInfo::class.java, "versionCode")

@RestrictedHiddenApi
private val longVersionCodeField by lazyReflectedField(
  ApplicationInfo::class.java, "longVersionCode"
)

val ApplicationInfo.longVersionCodeCompat: Long
  get() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      longVersionCodeField.getLong(this)
    } else {
      versionCodeField.getInt(this).toLong()
    }
