package io.github.nullptrx.files.provider.common.content

import android.os.Parcelable
import io.github.nullptrx.files.provider.common.ContentProviderFileAttributes
import java8.nio.file.attribute.FileTime

abstract class AbstractContentProviderFileAttributes : ContentProviderFileAttributes, Parcelable {
  protected abstract val lastModifiedTime: FileTime
  protected abstract val mimeType: String?
  protected abstract val size: Long
  protected abstract val fileKey: Parcelable

  override fun lastModifiedTime(): FileTime = lastModifiedTime

  override fun mimeType(): String? = mimeType

  override fun size(): Long = size

  override fun fileKey(): Parcelable = fileKey
}
