package io.github.nullptrx.files.provider.linux

import android.os.Parcel
import android.os.Parcelable
import io.github.nullptrx.files.compat.readBooleanCompat
import io.github.nullptrx.files.compat.readParcelableCompat
import io.github.nullptrx.files.compat.writeBooleanCompat
import io.github.nullptrx.files.provider.root.RootPosixFileAttributeView
import io.github.nullptrx.files.provider.root.RootablePosixFileAttributeView

internal class LinuxFileAttributeView constructor(
  private val path: LinuxPath,
  private val noFollowLinks: Boolean
) : RootablePosixFileAttributeView(
  path, LocalLinuxFileAttributeView(path.toByteString(), noFollowLinks),
  { RootPosixFileAttributeView(it) }
) {
  private constructor(source: Parcel) : this(
    source.readParcelableCompat()!!, source.readBooleanCompat()
  )

  override fun describeContents(): Int = 0

  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeParcelable(path, flags)
    dest.writeBooleanCompat(noFollowLinks)
  }

  companion object {
    val SUPPORTED_NAMES = LocalLinuxFileAttributeView.SUPPORTED_NAMES

    @JvmField
    val CREATOR = object : Parcelable.Creator<LinuxFileAttributeView> {
      override fun createFromParcel(source: Parcel): LinuxFileAttributeView =
        LinuxFileAttributeView(source)

      override fun newArray(size: Int): Array<LinuxFileAttributeView?> = arrayOfNulls(size)
    }
  }
}
