package io.github.nullptrx.files.provider.archive

import io.github.nullptrx.files.provider.common.posix.PosixFileAttributeView
import io.github.nullptrx.files.provider.common.posix.PosixFileModeBit
import io.github.nullptrx.files.provider.common.posix.PosixGroup
import io.github.nullptrx.files.provider.common.posix.PosixUser
import io.github.nullptrx.files.provider.common.protobuf.ByteString
import java8.nio.file.Path
import java8.nio.file.attribute.FileTime
import java.io.IOException

internal class LocalArchiveFileAttributeView(private val path: Path) : PosixFileAttributeView {
  override fun name(): String = NAME

  @Throws(IOException::class)
  override fun readAttributes(): ArchiveFileAttributes {
    val fileSystem = path.fileSystem as ArchiveFileSystem
    val entry = fileSystem.getEntryAsLocal(path)
    return ArchiveFileAttributes.from(fileSystem.archiveFile, entry)
  }

  override fun setTimes(
    lastModifiedTime: FileTime?,
    lastAccessTime: FileTime?,
    createTime: FileTime?
  ) {
    throw UnsupportedOperationException()
  }

  override fun setOwner(owner: PosixUser) {
    throw UnsupportedOperationException()
  }

  override fun setGroup(group: PosixGroup) {
    throw UnsupportedOperationException()
  }

  override fun setMode(mode: Set<PosixFileModeBit>) {
    throw UnsupportedOperationException()
  }

  override fun setSeLinuxContext(context: ByteString) {
    throw UnsupportedOperationException()
  }

  override fun restoreSeLinuxContext() {
    throw UnsupportedOperationException()
  }

  companion object {
    private val NAME = ArchiveFileSystemProvider.scheme

    val SUPPORTED_NAMES = setOf("basic", "posix", NAME)
  }
}
