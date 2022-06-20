package io.github.nullptrx.files.provider.remote

import io.github.nullptrx.files.FileSystemProviders
import io.github.nullptrx.files.provider.common.path.archiveRefresh
import java8.nio.file.FileSystem

open class RemoteFileServiceInterface : IRemoteFileService.Stub() {
  override fun getRemoteFileSystemProviderInterface(scheme: String): IRemoteFileSystemProvider {
    return RemoteFileSystemProviderInterface(FileSystemProviders[scheme])
  }

  override fun getRemoteFileSystemInterface(fileSystem: ParcelableObject): IRemoteFileSystem {
    return RemoteFileSystemInterface(fileSystem.value())
  }

  override fun getRemotePosixFileStoreInterface(
    fileStore: ParcelableObject
  ): IRemotePosixFileStore {
    return RemotePosixFileStoreInterface(fileStore.value())
  }

  override fun getRemotePosixFileAttributeViewInterface(
    attributeView: ParcelableObject
  ): IRemotePosixFileAttributeView {
    return RemotePosixFileAttributeViewInterface(attributeView.value())
  }


  override fun refreshArchiveFileSystem(fileSystem: ParcelableObject) {
    fileSystem.value<FileSystem>().getPath("").archiveRefresh()
  }

}
