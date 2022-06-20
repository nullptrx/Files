package io.github.nullptrx.files.example

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import io.github.nullptrx.files.provider.remote.IRemoteFileService

class LibSuConnection : ServiceConnection {
  private val TAG = "LibSu"


  override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
    Log.d(TAG, "AIDL onServiceConnected")
    val serviceInterface = IRemoteFileService.Stub.asInterface(service)
  }

  override fun onServiceDisconnected(name: ComponentName?) {
    Log.d(TAG, "AIDL onServiceDisconnected")
  }

  override fun onBindingDied(name: ComponentName?) {
    super.onBindingDied(name)
    Log.d(TAG, "AIDL onBindingDied")
  }

  override fun onNullBinding(name: ComponentName?) {
    super.onNullBinding(name)
    Log.d(TAG, "AIDL onNullBinding")
  }

}