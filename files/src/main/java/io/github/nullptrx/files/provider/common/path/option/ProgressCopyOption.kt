package io.github.nullptrx.files.provider.common.path.option

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import io.github.nullptrx.files.compat.readParcelableCompat
import io.github.nullptrx.files.provider.remote.RemoteCallback
import java8.nio.file.CopyOption
import kotlinx.parcelize.Parcelize

class ProgressCopyOption(
  val intervalMillis: Long,
  val listener: (Long) -> Unit
) : CopyOption, Parcelable {
  private constructor(source: Parcel) : this(
    source.readLong(),
    source.readParcelableCompat<RemoteCallback>()!!.let {
      { copiedSize -> it.sendResult(Bundle().putArgs(ListenerArgs(copiedSize))) }
    }
  )

  override fun describeContents(): Int = 0

  override fun writeToParcel(dest: Parcel, flags: Int) {
    dest.writeLong(intervalMillis)
    dest.writeParcelable(
      RemoteCallback { listener(it.getArgs<ListenerArgs>().copiedSize) }, flags
    )
  }

  companion object {
    @JvmField
    val CREATOR = object : Parcelable.Creator<ProgressCopyOption> {
      override fun createFromParcel(source: Parcel): ProgressCopyOption =
        ProgressCopyOption(source)

      override fun newArray(size: Int): Array<ProgressCopyOption?> = arrayOfNulls(size)
    }
  }

  @Parcelize
  private class ListenerArgs(val copiedSize: Long) : ParcelableArgs
}
