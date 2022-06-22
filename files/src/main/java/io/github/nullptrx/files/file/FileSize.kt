package io.github.nullptrx.files.file

import android.content.Context
import android.text.format.Formatter
import io.github.nullptrx.files.R
import io.github.nullptrx.files.extension.getQuantityString

@JvmInline
value class FileSize(val value: Long) {

  /* @see android.text.format.Formatter#formatBytes(Resources, long, int) */
  val isHumanReadableInBytes: Boolean
    get() = value <= 900

  fun formatInBytes(context: Context): String =
    context.getQuantityString(R.plurals.size_in_bytes_format, value.toInt(), value)

  fun formatHumanReadable(context: Context): String =
    Formatter.formatFileSize(context, value)
}

fun Long.asFileSize(): FileSize = FileSize(this)
