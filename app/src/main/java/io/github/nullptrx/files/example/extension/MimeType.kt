package io.github.nullptrx.files.example.extension

import io.github.nullptrx.files.example.ui.filelist.icon.MimeTypeIcon
import io.github.nullptrx.files.example.ui.filelist.icon.icon
import io.github.nullptrx.files.file.MimeType

val MimeType.isImage: Boolean
  get() = icon == MimeTypeIcon.IMAGE

val MimeType.isAudio: Boolean
  get() = icon == MimeTypeIcon.AUDIO

val MimeType.isVideo: Boolean
  get() = icon == MimeTypeIcon.VIDEO

val MimeType.isMedia: Boolean
  get() = isAudio || isVideo

val MimeType.isPdf: Boolean
  get() = this == MimeType.PDF