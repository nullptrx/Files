package io.github.nullptrx.files.example.extension

import android.content.Context
import android.os.Build
import io.github.nullptrx.files.example.ui.filelist.icon.getBrokenSymbolicLinkName
import io.github.nullptrx.files.example.ui.filelist.icon.getName
import io.github.nullptrx.files.extension.isApk
import io.github.nullptrx.files.file.FileItem
import io.github.nullptrx.files.file.MimeType
import io.github.nullptrx.files.file.extension
import io.github.nullptrx.files.provider.document.documentSupportsThumbnail
import io.github.nullptrx.files.provider.document.isDocumentPath
import io.github.nullptrx.files.provider.document.resolver.DocumentResolver
import io.github.nullptrx.files.provider.linux.isLinuxPath


fun FileItem.getMimeTypeName(context: Context): String {
  if (attributesNoFollowLinks.isSymbolicLink && isSymbolicLinkBroken) {
    return MimeType.getBrokenSymbolicLinkName(context)
  }
  return mimeType.getName(extension, context)
}

// @see PathAttributesFetcher.fetch
val FileItem.supportsThumbnail: Boolean
  get() {
    if (path.isDocumentPath && attributes.documentSupportsThumbnail) {
      return true
    }
    val isLocalPath = path.isLinuxPath
        || (path.isDocumentPath && DocumentResolver.isLocal(path as DocumentResolver.Path))
    // val shouldReadRemotePath = !path.isFtpPath
    //     && Settings.READ_REMOTE_FILES_FOR_THUMBNAIL.valueCompat
    // if (!(isLocalPath || shouldReadRemotePath)) {
    //   return false
    // }
    if (!isLocalPath) {
      return false
    }
    return when {
      mimeType.isApk && path.isGetPackageArchiveInfoCompatible -> true
      mimeType.isImage -> true
      mimeType.isMedia && (path.isLinuxPath || path.isDocumentPath) -> true
      mimeType.isPdf && (path.isLinuxPath || path.isDocumentPath) ->
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.P   // || Settings.SHOW_PDF_THUMBNAIL_PRE_28.valueCompat

      else -> false
    }
  }