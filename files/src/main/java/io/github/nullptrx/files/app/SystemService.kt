package io.github.nullptrx.files.app

import android.content.ContentResolver
import android.os.Process
import android.os.storage.StorageManager
import androidx.core.content.ContextCompat

internal val isRunningAsRoot = Process.myUid() == 0

internal val appClassLoader = AppProvider::class.java.classLoader

internal val contentResolver: ContentResolver by lazy { application.contentResolver }

internal val storageManager: StorageManager by lazy {
  ContextCompat.getSystemService(application, StorageManager::class.java)!!
}
