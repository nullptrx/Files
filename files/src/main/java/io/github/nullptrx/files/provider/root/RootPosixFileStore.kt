package io.github.nullptrx.files.provider.root

import io.github.nullptrx.files.provider.common.posix.PosixFileStore
import io.github.nullptrx.files.provider.remote.RemoteInterface
import io.github.nullptrx.files.provider.remote.RemotePosixFileStore

class RootPosixFileStore(fileStore: PosixFileStore) : RemotePosixFileStore(
  RemoteInterface { RootFileService.getRemotePosixFileStoreInterface(fileStore) }
)
