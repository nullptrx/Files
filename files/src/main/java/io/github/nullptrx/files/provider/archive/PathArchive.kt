package io.github.nullptrx.files.provider.common.path

import io.github.nullptrx.files.provider.archive.ArchiveFileSystemProvider
import io.github.nullptrx.files.provider.archive.ArchivePath
import java8.nio.file.Path
import java8.nio.file.ProviderMismatchException

val Path.archiveFile: Path
  get() {
    this as? ArchivePath ?: throw ProviderMismatchException(toString())
    return fileSystem.archiveFile
  }

fun Path.archiveRefresh() {
  this as? ArchivePath ?: throw ProviderMismatchException(toString())
  fileSystem.refresh()
}

fun Path.createArchiveRootPath(): Path =
  ArchiveFileSystemProvider.getOrNewFileSystem(this).rootDirectory
