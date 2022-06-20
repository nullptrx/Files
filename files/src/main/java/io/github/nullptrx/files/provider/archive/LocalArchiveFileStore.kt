package io.github.nullptrx.files.provider.archive

import io.github.nullptrx.files.file.MimeType
import io.github.nullptrx.files.file.guessFromPath
import io.github.nullptrx.files.provider.common.path.size
import io.github.nullptrx.files.provider.common.posix.PosixFileStore
import java8.nio.file.Path
import java8.nio.file.attribute.FileAttributeView
import org.tukaani.xz.UnsupportedOptionsException
import java.io.IOException

internal class LocalArchiveFileStore(private val archiveFile: Path) : PosixFileStore() {
  override fun refresh() {}

  override fun name(): String = archiveFile.toString()

  override fun type(): String = MimeType.guessFromPath(archiveFile.toString()).value

  override fun isReadOnly(): Boolean = true

  @Throws(IOException::class)
  override fun setReadOnly(readOnly: Boolean) {
    throw UnsupportedOptionsException()
  }

  @Throws(IOException::class)
  override fun getTotalSpace(): Long = archiveFile.size()

  override fun getUsableSpace(): Long = 0

  override fun getUnallocatedSpace(): Long = 0

  override fun supportsFileAttributeView(type: Class<out FileAttributeView>): Boolean =
    ArchiveFileSystemProvider.supportsFileAttributeView(type)

  override fun supportsFileAttributeView(name: String): Boolean =
    name in ArchiveFileAttributeView.SUPPORTED_NAMES
}
