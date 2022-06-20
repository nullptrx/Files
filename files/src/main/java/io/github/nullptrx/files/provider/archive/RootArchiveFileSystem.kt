package io.github.nullptrx.files.provider.archive

import io.github.nullptrx.files.provider.common.RemoteFileSystemException
import io.github.nullptrx.files.provider.root.RootFileService
import io.github.nullptrx.files.provider.root.RootFileSystem
import java8.nio.file.FileSystem

internal class RootArchiveFileSystem(
  private val fileSystem: FileSystem
) : RootFileSystem(fileSystem) {
  private var isRefreshNeeded = false

  private val lock = Any()

  fun refresh() {
    synchronized(lock) {
      if (hasRemoteInterface()) {
        isRefreshNeeded = true
      }
    }
  }

  @Throws(RemoteFileSystemException::class)
  fun doRefreshIfNeeded() {
    synchronized(lock) {
      if (isRefreshNeeded) {
        if (hasRemoteInterface()) {
          RootFileService.refreshArchiveFileSystem(fileSystem)
        }
        isRefreshNeeded = false
      }
    }
  }
}
