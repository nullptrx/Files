package io.github.nullptrx.files.extension

import android.media.MediaMetadataRetriever
import io.github.nullptrx.files.file.MimeType
import io.github.nullptrx.files.provider.archive.isArchivePath
import io.github.nullptrx.files.provider.common.path.archiveFile
import io.github.nullptrx.files.provider.document.isDocumentPath
import io.github.nullptrx.files.provider.document.resolver.DocumentResolver
import io.github.nullptrx.files.provider.linux.isLinuxPath
import java8.nio.file.Path

val Path.name: String
  get() = fileName?.toString() ?: if (isArchivePath) archiveFile.fileName.toString() else "/"

fun Path.toUserFriendlyString(): String = if (isLinuxPath) toFile().path else toUri().toString()

fun Path.isArchiveFile(mimeType: MimeType): Boolean = !isArchivePath && mimeType.isSupportedArchive

val Path.isMediaMetadataRetrieverCompatible: Boolean
  get() = isLinuxPath || isDocumentPath

fun MediaMetadataRetriever.setDataSource(path: Path) {
  when {
    path.isLinuxPath -> setDataSource(path.toFile().path)
    path.isDocumentPath ->
      DocumentResolver.openParcelFileDescriptor(path as DocumentResolver.Path, "r")
        .use { pfd -> setDataSource(pfd.fileDescriptor) }
    else -> throw IllegalArgumentException(path.toString())
  }
}