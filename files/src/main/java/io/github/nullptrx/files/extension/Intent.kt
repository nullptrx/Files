package io.github.nullptrx.files.extension

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import io.github.nullptrx.files.app.appClassLoader
import io.github.nullptrx.files.app.application
import kotlin.reflect.KClass

fun <T : Context> KClass<T>.createIntent(): Intent = Intent(application, java)

fun <T : Parcelable> Intent.getParcelableExtraSafe(key: String?): T? {
  setExtrasClassLoader(appClassLoader)
  return getParcelableExtra(key)
}