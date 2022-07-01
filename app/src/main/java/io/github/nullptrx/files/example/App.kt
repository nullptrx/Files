package io.github.nullptrx.files.example

import android.app.Application
import android.content.Context
import android.os.Build
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.decode.SvgDecoder
import io.github.nullptrx.files.example.app.application
import io.github.nullptrx.files.example.app.backgroundActivityStartNotificationTemplate
import io.github.nullptrx.files.example.app.notificationManager
import io.github.nullptrx.files.example.coil.*
import io.github.nullptrx.files.example.notification.fileJobNotificationTemplate
import io.github.nullptrx.files.example.notification.ftpServerServiceNotificationTemplate

class App : Application() {

  override fun attachBaseContext(base: Context?) {
    super.attachBaseContext(base)
  }

  override fun onCreate() {
    super.onCreate()

    initializeCoil()

  }

  override fun onTerminate() {
    super.onTerminate()
  }
}


fun initializeCoil() {
  Coil.setImageLoader(
    ImageLoader.Builder(application)
      .components {
        add(AppIconApplicationInfoKeyer())
        add(AppIconApplicationInfoFetcherFactory(application))
        add(AppIconPackageNameKeyer())
        add(AppIconPackageNameFetcherFactory(application))
        add(PathAttributesKeyer())
        add(PathAttributesFetcher.Factory(application))
        add(
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoderDecoder.Factory()
          } else {
            GifDecoder.Factory()
          }
        )
        add(SvgDecoder.Factory(false))
      }
      .build()
  )
}

private fun createNotificationChannels() {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    notificationManager.createNotificationChannels(
      listOf(
        backgroundActivityStartNotificationTemplate.channelTemplate,
        fileJobNotificationTemplate.channelTemplate,
        ftpServerServiceNotificationTemplate.channelTemplate
      ).map { it.create(application) }
    )
  }
}
