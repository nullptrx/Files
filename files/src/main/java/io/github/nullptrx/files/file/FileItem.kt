package io.github.nullptrx.files.file

import android.os.Parcelable
import androidx.annotation.WorkerThread
import io.github.nullptrx.files.extension.name
import io.github.nullptrx.files.provider.common.AndroidFileTypeDetector
import io.github.nullptrx.files.provider.common.path.isHidden
import io.github.nullptrx.files.provider.common.path.readAttributes
import io.github.nullptrx.files.provider.common.path.readSymbolicLinkByteString
import io.github.nullptrx.files.provider.common.protobuf.ByteStringBuilder
import io.github.nullptrx.files.provider.common.protobuf.toByteString
import io.github.nullptrx.files.provider.remote.ParcelableParceler
import java8.nio.file.LinkOption
import java8.nio.file.Path
import java8.nio.file.attribute.BasicFileAttributes
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import java.io.IOException
import java.text.CollationKey
import java.text.Collator

@Parcelize
data class FileItem(
  val path: @WriteWith<ParcelableParceler> Path,
  val nameCollationKey: @WriteWith<ParcelableParceler> CollationKey,
  val attributesNoFollowLinks: @WriteWith<ParcelableParceler> BasicFileAttributes,
  val symbolicLinkTarget: String?,
  private val symbolicLinkTargetAttributes: @WriteWith<ParcelableParceler> BasicFileAttributes?,
  val isHidden: Boolean,
  val mimeType: MimeType
) : Parcelable {
  val attributes: BasicFileAttributes
    get() = symbolicLinkTargetAttributes ?: attributesNoFollowLinks

  val isSymbolicLinkBroken: Boolean
    get() {
      check(attributesNoFollowLinks.isSymbolicLink) { "Not a symbolic link" }
      return symbolicLinkTargetAttributes == null
    }
}

@WorkerThread
@Throws(IOException::class)
fun Path.loadFileItem(): FileItem {
  val nameCollationKey = Collator.getInstance().getCollationKeyForFileName(name)
  val attributes = readAttributes(BasicFileAttributes::class.java, LinkOption.NOFOLLOW_LINKS)
  val isHidden = isHidden
  if (!attributes.isSymbolicLink) {
    val mimeType = AndroidFileTypeDetector.getMimeType(this, attributes).asMimeType()
    return FileItem(this, nameCollationKey, attributes, null, null, isHidden, mimeType)
  }
  val symbolicLinkTarget = readSymbolicLinkByteString().toString()
  val symbolicLinkTargetAttributes = try {
    readAttributes(BasicFileAttributes::class.java)
  } catch (e: IOException) {
    e.printStackTrace()
    null
  }
  val mimeType = AndroidFileTypeDetector.getMimeType(
    this, symbolicLinkTargetAttributes ?: attributes
  ).asMimeType()
  return FileItem(
    this, nameCollationKey, attributes, symbolicLinkTarget, symbolicLinkTargetAttributes,
    isHidden, mimeType
  )
}


private val COLLATION_SENTINEL = byteArrayOf(1, 1, 1)

// @see https://github.com/GNOME/glib/blob/mainline/glib/gunicollate.c
//      g_utf8_collate_key_for_filename()
fun Collator.getCollationKeyForFileName(source: String): CollationKey {
  val result = ByteStringBuilder()
  val suffix = ByteStringBuilder()
  var previousIndex = 0
  var index = 0
  val endIndex = source.length
  while (index < endIndex) {
    when {
      source[index] == '.' -> {
        if (previousIndex != index) {
          val collationKey = getCollationKey(source.substring(previousIndex, index))
          result.append(collationKey.toByteArray())
        }
        result.append(COLLATION_SENTINEL).append(1)
        previousIndex = index + 1
      }
      source[index].isAsciiDigit() -> {
        if (previousIndex != index) {
          val collationKey = getCollationKey(source.substring(previousIndex, index))
          result.append(collationKey.toByteArray())
        }
        result.append(COLLATION_SENTINEL).append(2)
        previousIndex = index
        var leadingZeros: Int
        var digits: Int
        if (source[index] == '0') {
          leadingZeros = 1
          digits = 0
        } else {
          leadingZeros = 0
          digits = 1
        }
        while (++index < endIndex) {
          if (source[index] == '0' && digits == 0) {
            ++leadingZeros
          } else if (source[index].isAsciiDigit()) {
            ++digits
          } else {
            if (digits == 0) {
              ++digits
              --leadingZeros
            }
            break
          }
        }
        while (digits > 1) {
          result.append(':'.code.toByte())
          --digits
        }
        if (leadingZeros > 0) {
          suffix.append(leadingZeros.toByte())
          previousIndex += leadingZeros
        }
        result.append(source.substring(previousIndex, index).toByteString())
        previousIndex = index
        --index
      }
      else -> {}
    }
    ++index
  }
  if (previousIndex != index) {
    val collationKey = getCollationKey(source.substring(previousIndex, index))
    result.append(collationKey.toByteArray())
  }
  result.append(suffix.toByteString())
  return ByteArrayCollationKey(source, result.toByteString().borrowBytes())
}

private fun Char.isAsciiDigit(): Boolean = this in '0'..'9'

@Parcelize
private class ByteArrayCollationKey(
  @Suppress("CanBeParameter")
  private val source: String,
  private val bytes: ByteArray
) : CollationKey(source), Parcelable {
  override fun compareTo(other: CollationKey): Int {
    other as ByteArrayCollationKey
    return bytes.unsignedCompareTo(other.bytes)
  }

  override fun toByteArray(): ByteArray = bytes.copyOf()
}

private fun ByteArray.unsignedCompareTo(other: ByteArray): Int {
  val size = size
  val otherSize = other.size
  for (index in 0 until kotlin.math.min(size, otherSize)) {
    val byte = this[index].toInt() and 0xFF
    val otherByte = other[index].toInt() and 0xFF
    if (byte < otherByte) {
      return -1
    } else if (byte > otherByte) {
      return 1
    }
  }
  return size - otherSize
}
