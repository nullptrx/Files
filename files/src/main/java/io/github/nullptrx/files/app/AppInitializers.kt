package io.github.nullptrx.files.app

import android.webkit.WebView
import com.jakewharton.threetenabp.AndroidThreeTen
import io.github.nullptrx.files.BuildConfig
import io.github.nullptrx.files.FileSystemProviders
import io.github.nullptrx.files.hiddenapi.HiddenApi
import io.github.nullptrx.files.settings.Settings

val appInitializers = listOf(
  ::disableHiddenApiChecks, ::initializeThreeTen,
  ::initializeWebViewDebugging,
  ::initializeFileSystemProviders, ::initializeLiveDataObjects,
)


private fun disableHiddenApiChecks() {
  HiddenApi.disableHiddenApiChecks()
}

private fun initializeThreeTen() {
  AndroidThreeTen.init(application)
}

private fun initializeWebViewDebugging() {
  if (BuildConfig.DEBUG) {
    WebView.setWebContentsDebuggingEnabled(true)
  }
}

private fun initializeFileSystemProviders() {
  FileSystemProviders.install()
  FileSystemProviders.overflowWatchEvents = true
  // SingletonContext.init() calls NameServiceClientImpl.initCache() which connects to network.
  // AsyncTask.THREAD_POOL_EXECUTOR.execute {
  //     SingletonContext.init(
  //         Properties().apply {
  //             setProperty("jcifs.netbios.cachePolicy", "0")
  //             setProperty("jcifs.smb.client.maxVersion", "SMB1")
  //         }
  //     )
  // }
  // FtpClient.authenticator = FtpServerAuthenticator
  // SftpClient.authenticator = SftpServerAuthenticator
  // SmbClient.authenticator = SmbServerAuthenticator
}

private fun initializeLiveDataObjects() {
  // Force initialization of LiveData objects so that it won't happen on a background thread.
  // StorageVolumeListLiveData.value
  Settings.FILE_LIST_DEFAULT_DIRECTORY.value
}

