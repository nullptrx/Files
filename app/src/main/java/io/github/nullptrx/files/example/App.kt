package io.github.nullptrx.files.example

import android.app.Application
import android.content.Context

class App : Application() {

  override fun attachBaseContext(base: Context?) {
    super.attachBaseContext(base)
  }

  override fun onCreate() {
    super.onCreate()
  }

  override fun onTerminate() {
    super.onTerminate()
  }
}