package io.github.nullptrx.files.app

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Build

lateinit var application: Application private set

val applicationVersionCode: Long
  get() {
    val info = application.packageManager.getPackageInfo(application.packageName, 0)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      info.longVersionCode
    } else {
      info.versionCode.toLong()
    }
  }


class AppProvider : ContentProvider() {

  override fun onCreate(): Boolean {
    application = context as Application
    appInitializers.forEach { it() }
    return true
  }

  override fun query(
    uri: Uri,
    projection: Array<String?>?,
    selection: String?,
    selectionArgs: Array<String?>?,
    sortOrder: String?
  ): Cursor? {
    throw UnsupportedOperationException()
  }

  override fun getType(uri: Uri): String? {
    throw UnsupportedOperationException()
  }

  override fun insert(uri: Uri, values: ContentValues?): Uri? {
    throw UnsupportedOperationException()
  }

  override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String?>?): Int {
    throw UnsupportedOperationException()
  }

  override fun update(
    uri: Uri,
    values: ContentValues?,
    selection: String?,
    selectionArgs: Array<String?>?
  ): Int {
    throw UnsupportedOperationException()
  }
}

// object GlobalFiles : CoroutineScope by CoroutineScope(Dispatchers.IO) {
//   val application: Application
//     get() = application_
//
//   val packageName: String by lazy {
//     application.packageName
//   }
//
//   val versionCode: Long by lazy {
//     val info = application.packageManager.getPackageInfo(application.packageName, 0)
//     return@lazy if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//       info.longVersionCode
//     } else {
//       info.versionCode.toLong()
//     }
//   }
//
//   private lateinit var application_: Application
//
//   fun init(application: Application) {
//     this.application_ = application
//   }
//
//   fun destroy() {
//     cancel()
//   }
// }
