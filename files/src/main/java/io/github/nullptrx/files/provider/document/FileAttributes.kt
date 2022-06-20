package io.github.nullptrx.files.provider.document

import android.provider.DocumentsContract
import io.github.nullptrx.files.extension.hasBits
import java8.nio.file.ProviderMismatchException
import java8.nio.file.attribute.BasicFileAttributes

val BasicFileAttributes.documentSupportsThumbnail: Boolean
  get() {
    this as? DocumentFileAttributes ?: throw ProviderMismatchException(toString())
    return flags().hasBits(DocumentsContract.Document.FLAG_SUPPORTS_THUMBNAIL)
  }
