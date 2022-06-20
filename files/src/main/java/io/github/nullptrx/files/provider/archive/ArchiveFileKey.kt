package io.github.nullptrx.files.provider.archive

import android.os.Parcelable
import io.github.nullptrx.files.provider.remote.ParcelableParceler
import java8.nio.file.Path
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith

@Parcelize
internal data class ArchiveFileKey(
  private val archiveFile: @WriteWith<ParcelableParceler> Path,
  private val entryName: String
) : Parcelable
