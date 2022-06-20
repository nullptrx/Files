package io.github.nullptrx.files.provider.root

import io.github.nullptrx.files.provider.common.posix.PosixFileAttributeView
import io.github.nullptrx.files.provider.remote.RemoteInterface
import io.github.nullptrx.files.provider.remote.RemotePosixFileAttributeView

open class RootPosixFileAttributeView(
  attributeView: PosixFileAttributeView
) : RemotePosixFileAttributeView(
  RemoteInterface { RootFileService.getRemotePosixFileAttributeViewInterface(attributeView) }
) {
  override fun name(): String {
    throw AssertionError()
  }
}

