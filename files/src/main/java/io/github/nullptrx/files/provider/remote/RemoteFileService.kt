package io.github.nullptrx.files.provider.remote

import io.github.nullptrx.files.provider.common.RemoteFileSystemException
import io.github.nullptrx.files.provider.common.posix.PosixFileAttributeView
import io.github.nullptrx.files.provider.common.posix.PosixFileStore
import java8.nio.file.FileSystem

abstract class RemoteFileService(private val remoteInterface: RemoteInterface<IRemoteFileService>) {
  @Throws(RemoteFileSystemException::class)
  fun getRemoteFileSystemProviderInterface(scheme: String): IRemoteFileSystemProvider {
    return remoteInterface.get().call { getRemoteFileSystemProviderInterface(scheme) }
  }


  @Throws(RemoteFileSystemException::class)
  fun getRemoteFileSystemInterface(fileSystem: FileSystem): IRemoteFileSystem =
    remoteInterface.get().call { getRemoteFileSystemInterface(fileSystem.toParcelable()) }

  @Throws(RemoteFileSystemException::class)
  fun getRemotePosixFileStoreInterface(fileStore: PosixFileStore): IRemotePosixFileStore =
    remoteInterface.get().call { getRemotePosixFileStoreInterface(fileStore.toParcelable()) }

  @Throws(RemoteFileSystemException::class)
  fun getRemotePosixFileAttributeViewInterface(
    attributeView: PosixFileAttributeView
  ): IRemotePosixFileAttributeView =
    remoteInterface.get().call {
      getRemotePosixFileAttributeViewInterface(attributeView.toParcelable())
    }

  @Throws(RemoteFileSystemException::class)
  fun refreshArchiveFileSystem(fileSystem: FileSystem) {
    remoteInterface.get().call { refreshArchiveFileSystem(fileSystem.toParcelable()) }
  }
}
