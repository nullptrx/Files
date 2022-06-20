package io.github.nullptrx.files.app

import android.content.ContentResolver
import android.os.Process
import android.os.storage.StorageManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

val isRunningAsRoot = Process.myUid() == 0

val appClassLoader = AppProvider::class.java.classLoader

val contentResolver: ContentResolver by lazy { application.contentResolver }

val storageManager: StorageManager by lazy {
  ContextCompat.getSystemService(application, StorageManager::class.java)!!
}

val notificationManager: NotificationManagerCompat by lazy {
  NotificationManagerCompat.from(application)
}