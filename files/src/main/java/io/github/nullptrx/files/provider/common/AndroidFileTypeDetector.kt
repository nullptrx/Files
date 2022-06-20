package io.github.nullptrx.files.provider.common

import io.github.nullptrx.files.file.MimeType
import io.github.nullptrx.files.file.forSpecialPosixFileType
import io.github.nullptrx.files.file.guessFromPath
import io.github.nullptrx.files.provider.common.path.readAttributes
import io.github.nullptrx.files.provider.common.posix.posixFileType
import java8.nio.file.Path
import java8.nio.file.attribute.BasicFileAttributes
import java8.nio.file.spi.FileTypeDetector
import java.io.IOException

object AndroidFileTypeDetector : FileTypeDetector() {
  @Throws(IOException::class)
  override fun probeContentType(path: Path): String {
    val attributes = path.readAttributes(BasicFileAttributes::class.java)
    return getMimeType(path, attributes)
  }

  fun getMimeType(path: Path, attributes: BasicFileAttributes): String {
    MimeType.forSpecialPosixFileType(attributes.posixFileType)?.let { return it.value }
    if (attributes.isDirectory) {
      return MimeType.DIRECTORY.value
    }
    if (attributes is ContentProviderFileAttributes) {
      attributes.mimeType()?.let { return it }
    }
    return MimeType.guessFromPath(path.toString()).value
  }
}