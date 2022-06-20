package io.github.nullptrx.files

import io.github.nullptrx.files.app.isRunningAsRoot
import io.github.nullptrx.files.provider.archive.ArchiveFileSystemProvider
import io.github.nullptrx.files.provider.common.AndroidFileTypeDetector
import io.github.nullptrx.files.provider.content.ContentFileSystemProvider
import io.github.nullptrx.files.provider.document.DocumentFileSystemProvider
import io.github.nullptrx.files.provider.linux.LinuxFileSystemProvider
import java8.nio.file.Files
import java8.nio.file.ProviderNotFoundException
import java8.nio.file.spi.FileSystemProvider

object FileSystemProviders {
  /**
   * If set, WatchService implementations will skip processing any event data and simply send an
   * overflow event to all the registered keys upon successful read from the inotify fd. This can
   * help reducing the JNI and GC overhead when large amount of inotify events are generated.
   * Simply sending an overflow event to all the keys is okay because we use only one key per
   * service for WatchServicePathObservable.
   */
  @Volatile
  var overflowWatchEvents = false

  fun install() {
    FileSystemProvider.installDefaultProvider(LinuxFileSystemProvider)
    FileSystemProvider.installProvider(ArchiveFileSystemProvider)
    if (!isRunningAsRoot) {
      FileSystemProvider.installProvider(ContentFileSystemProvider)
      FileSystemProvider.installProvider(DocumentFileSystemProvider)
      //     FileSystemProvider.installProvider(FtpFileSystemProvider)
      //     FileSystemProvider.installProvider(FtpsFileSystemProvider)
      //     FileSystemProvider.installProvider(FtpesFileSystemProvider)
      //     FileSystemProvider.installProvider(SftpFileSystemProvider)
      //     FileSystemProvider.installProvider(SmbFileSystemProvider)
    }
    Files.installFileTypeDetector(AndroidFileTypeDetector)
  }

  operator fun get(scheme: String): FileSystemProvider {
    for (provider in FileSystemProvider.installedProviders()) {
      if (provider.scheme.equals(scheme, ignoreCase = true)) {
        return provider
      }
    }
    throw ProviderNotFoundException(scheme)
  }

  fun providers(): Array<String> {
    val schemes = mutableListOf<String>()
    for (provider in FileSystemProvider.installedProviders()) {
      schemes.add(provider.scheme)
    }
    return schemes.toTypedArray()
  }
}