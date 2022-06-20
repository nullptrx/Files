package io.github.nullptrx.files.provider.archive

import android.os.Parcel
import android.os.Parcelable
import io.github.nullptrx.files.compat.readParcelableCompat
import io.github.nullptrx.files.provider.root.RootablePosixFileAttributeView

internal class ArchiveFileAttributeView(
  private val path: ArchivePath
) : RootablePosixFileAttributeView(
  path, LocalArchiveFileAttributeView(path), { RootArchiveFileAttributeView(it, path) }
) {
  private constructor(source: Parcel) : this(source.readParcelableCompat<ArchivePath>()!!)

  override fun describeContents(): Int = 0

  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeParcelable(path as Parcelable, flags)
  }

  companion object {
    val SUPPORTED_NAMES = LocalArchiveFileAttributeView.SUPPORTED_NAMES

    @JvmField
    val CREATOR = object : Parcelable.Creator<ArchiveFileAttributeView> {
      override fun createFromParcel(source: Parcel): ArchiveFileAttributeView =
        ArchiveFileAttributeView(source)

      override fun newArray(size: Int): Array<ArchiveFileAttributeView?> = arrayOfNulls(size)
    }
  }
}
