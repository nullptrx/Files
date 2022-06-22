package io.github.nullptrx.files.example.service

import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.topjohnwu.superuser.ipc.RootService
import io.github.nullptrx.files.provider.remote.RemoteFileServiceInterface
import java.util.*

class LibSuFileService : RootService() {
  private val TAG = "LibSu"


  override fun onBind(intent: Intent): IBinder {
    Log.d(TAG, "LibSuFileService: onBind")
    return RemoteFileServiceInterface()
  }

  private val uuid = UUID.randomUUID().toString()

  override fun onCreate() {
    Log.d(TAG, "LibSuFileService: onCreate, $uuid")
  }

  // override fun onRebind(intent: Intent) {
  //   // This callback will be called when we are reusing a previously started root process
  //   Log.d(TAG, "LibSuFileService: onRebind, daemon process reused")
  // }
  //
  // override fun onUnbind(intent: Intent): Boolean {
  //   Log.d(TAG, "LibSuFileService: onUnbind, client process unbound")
  //   // Return true here so onRebind will be called
  //   return true
  // }
  //
  // override fun onDestroy() {
  //   Log.d(TAG, "LibSuFileService: onDestroy")
  // }
}