package io.github.nullptrx.files.provider.archive

import io.github.nullptrx.files.provider.common.posix.PosixFileAttributeView
import io.github.nullptrx.files.provider.common.posix.PosixFileAttributes
import io.github.nullptrx.files.provider.root.RootPosixFileAttributeView
import java8.nio.file.Path
import java.io.IOException

internal class RootArchiveFileAttributeView(
  attributeView: PosixFileAttributeView,
  private val path: Path
) : RootPosixFileAttributeView(attributeView) {
  @Throws(IOException::class)
  override fun readAttributes(): PosixFileAttributes {
    ArchiveFileSystemProvider.doRefreshIfNeeded(path)
    return super.readAttributes()
  }
}
