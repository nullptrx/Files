package io.github.nullptrx.files.extension

import android.os.ParcelFileDescriptor
import android.system.OsConstants
import io.github.nullptrx.files.compat.NioUtilsCompat
import io.github.nullptrx.files.provider.linux.syscall.SyscallException
import io.github.nullptrx.files.provider.linux.syscall.Syscalls
import java8.nio.channels.FileChannel
import java8.nio.channels.FileChannels
import java.io.Closeable
import java.io.FileDescriptor
import java.io.IOException
import kotlin.reflect.KClass

fun KClass<FileChannel>.open(fd: FileDescriptor, flags: Int): FileChannel {
  val closeable = Closeable {
    try {
      Syscalls.close(fd)
    } catch (e: SyscallException) {
      throw IOException(e)
    }
  }
  return FileChannels.from(NioUtilsCompat.newFileChannel(closeable, fd, flags))
}

fun KClass<FileChannel>.open(pfd: ParcelFileDescriptor, mode: String): FileChannel =
  FileChannels.from(
    NioUtilsCompat.newFileChannel(
      pfd, pfd.fileDescriptor,
      ParcelFileDescriptor::class.modeToFlags(ParcelFileDescriptor.parseMode(mode))
    )
  )


// @see android.os.FileUtils#translateModePfdToPosix
fun KClass<ParcelFileDescriptor>.modeToFlags(mode: Int): Int {
  var flags = when {
    mode.hasBits(ParcelFileDescriptor.MODE_READ_WRITE) -> OsConstants.O_RDWR
    mode.hasBits(ParcelFileDescriptor.MODE_WRITE_ONLY) -> OsConstants.O_WRONLY
    mode.hasBits(ParcelFileDescriptor.MODE_READ_ONLY) -> OsConstants.O_RDONLY
    else -> throw IllegalArgumentException(mode.toString())
  }
  if (mode.hasBits(ParcelFileDescriptor.MODE_CREATE)) {
    flags = flags or OsConstants.O_CREAT
  }
  if (mode.hasBits(ParcelFileDescriptor.MODE_TRUNCATE)) {
    flags = flags or OsConstants.O_TRUNC
  }
  if (mode.hasBits(ParcelFileDescriptor.MODE_APPEND)) {
    flags = flags or OsConstants.O_APPEND
  }
  return flags
}
