package io.github.nullptrx.files.file

import io.github.nullptrx.files.extension.isArchiveFile
import io.github.nullptrx.files.extension.name
import io.github.nullptrx.files.provider.common.path.createArchiveRootPath
import io.github.nullptrx.files.provider.common.protobuf.asFileName
import java8.nio.file.Path
import java8.nio.file.attribute.BasicFileAttributes
import java8.nio.file.attribute.FileTime
import java.text.CollationKey


val FileItem.name: String
  get() = path.name

val FileItem.baseName: String
  get() = if (attributes.isDirectory) name else name.asFileName().baseName

val FileItem.extension: String
  get() = if (attributes.isDirectory) "" else name.asFileName().extensions


val FileItem.isArchiveFile: Boolean
  get() = path.isArchiveFile(mimeType)

val FileItem.isListable: Boolean
  get() = attributes.isDirectory || isArchiveFile

val FileItem.listablePath: Path
  get() = if (isArchiveFile) path.createArchiveRootPath() else path

// @see android.content.pm.parsing.ApkLiteParseUtils.parsePackageSplitNames
// @see android.content.pm.parsing.ParsingPackageUtils.validateName
// @see com.android.server.pm.PackageManagerService.getNextCodePath
private const val PACKAGE_NAME_COMPONENT_PATTERN = "[A-Za-z][0-9A-Z_a-z]*"
private const val PACKAGE_NAME_PATTERN =
  "$PACKAGE_NAME_COMPONENT_PATTERN(?:\\.$PACKAGE_NAME_COMPONENT_PATTERN)+"
private const val BASE64_URL_SAFE_CHARACTER_CLASS = "[0-9A-Za-z\\-_]"
private const val BASE64_URL_SAFE_PATTERN = ("(?:$BASE64_URL_SAFE_CHARACTER_CLASS{4})*"
    + "(?:$BASE64_URL_SAFE_CHARACTER_CLASS{3}=|$BASE64_URL_SAFE_CHARACTER_CLASS{2}==)?")
private val APP_DIRECTORY_REGEX =
  Regex("($PACKAGE_NAME_PATTERN)(?:-$BASE64_URL_SAFE_PATTERN)?")

val FileItem.appDirectoryPackageName: String?
  get() {
    if (!attributes.isDirectory) {
      return null
    }
    return APP_DIRECTORY_REGEX.matchEntire(name)?.groupValues?.get(1)
  }

fun FileItem.createDummyArchiveRoot(): FileItem =
  FileItem(
    path.createArchiveRootPath(), DummyCollationKey(), DummyArchiveRootBasicFileAttributes(),
    null, null, false, MimeType.DIRECTORY
  )

// Dummy collation key only to be added to the selection set, which may be used to determine file
// type when confirming deletion.
private class DummyCollationKey : CollationKey("") {
  override fun compareTo(other: CollationKey?): Int {
    throw UnsupportedOperationException()
  }

  override fun toByteArray(): ByteArray {
    throw UnsupportedOperationException()
  }
}

// Dummy attributes only to be added to the selection set, which may be used to determine file
// type when confirming deletion.
private class DummyArchiveRootBasicFileAttributes : BasicFileAttributes {
  override fun lastModifiedTime(): FileTime {
    throw UnsupportedOperationException()
  }

  override fun lastAccessTime(): FileTime {
    throw UnsupportedOperationException()
  }

  override fun creationTime(): FileTime {
    throw UnsupportedOperationException()
  }

  override fun isRegularFile(): Boolean = false

  override fun isDirectory(): Boolean = true

  override fun isSymbolicLink(): Boolean = false

  override fun isOther(): Boolean = false

  override fun size(): Long {
    throw UnsupportedOperationException()
  }

  override fun fileKey(): Any {
    throw UnsupportedOperationException()
  }
}