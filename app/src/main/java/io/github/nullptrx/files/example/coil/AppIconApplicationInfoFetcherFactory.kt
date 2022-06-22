package io.github.nullptrx.files.example.coil

import android.content.Context
import android.content.pm.ApplicationInfo
import coil.key.Keyer
import coil.request.Options
import io.github.nullptrx.files.example.R
import io.github.nullptrx.files.example.extension.getDimensionPixelSize
import io.github.nullptrx.files.example.extension.longVersionCodeCompat
import me.zhanghai.android.appiconloader.AppIconLoader
import java.io.Closeable

class AppIconApplicationInfoKeyer : Keyer<ApplicationInfo> {
  override fun key(data: ApplicationInfo, options: Options): String =
    AppIconLoader.getIconKey(data, data.longVersionCodeCompat, options.context)
}

class AppIconApplicationInfoFetcherFactory(
  context: Context
) : AppIconFetcher.Factory<ApplicationInfo>(
  // This is used by PrincipalListAdapter.
  context.getDimensionPixelSize(R.dimen.icon_size), context
) {
  override fun getApplicationInfo(data: ApplicationInfo): Pair<ApplicationInfo, Closeable?> =
    data to null
}

