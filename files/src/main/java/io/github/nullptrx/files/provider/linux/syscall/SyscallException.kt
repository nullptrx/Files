package io.github.nullptrx.files.provider.linux.syscall

import android.system.ErrnoException
import android.system.OsConstants
import io.github.nullptrx.files.hiddenapi.RestrictedHiddenApi
import io.github.nullptrx.files.provider.common.InvalidFileNameException
import io.github.nullptrx.files.provider.common.IsDirectoryException
import io.github.nullptrx.files.provider.common.ReadOnlyFileSystemException
import io.github.nullptrx.files.util.lazyReflectedField
import java8.nio.file.*

@RestrictedHiddenApi
private val functionNameField by lazyReflectedField(ErrnoException::class.java, "functionName")

val ErrnoException.functionNameCompat: String
  get() = functionNameField.get(this) as String

class SyscallException @JvmOverloads constructor(
  val functionName: String,
  val errno: Int,
  cause: Throwable? = null
) : Exception(perror(errno, functionName), cause) {

  constructor(errnoException: ErrnoException) : this(
    errnoException.functionNameCompat, errnoException.errno, errnoException
  )

  @Throws(AtomicMoveNotSupportedException::class)
  fun maybeThrowAtomicMoveNotSupportedException(file: String?, other: String?) {
    if (errno == OsConstants.EXDEV) {
      throw AtomicMoveNotSupportedException(file, other, message)
        .apply { initCause(this@SyscallException) }
    }
  }

  @Throws(InvalidFileNameException::class)
  fun maybeThrowInvalidFileNameException(file: String?) {
    if (errno == OsConstants.EINVAL) {
      throw InvalidFileNameException(file, null, message)
        .apply { initCause(this@SyscallException) }
    }
  }

  @Throws(NotLinkException::class)
  fun maybeThrowNotLinkException(file: String?) {
    if (errno == OsConstants.EINVAL) {
      throw InvalidFileNameException(file, null, message)
        .apply { initCause(this@SyscallException) }
    }
  }

  fun toFileSystemException(file: String?, other: String? = null): FileSystemException =
    when (errno) {
      OsConstants.EACCES, OsConstants.EPERM -> AccessDeniedException(file, other, message)
      OsConstants.EEXIST -> FileAlreadyExistsException(file, other, message)
      OsConstants.EISDIR -> IsDirectoryException(file, other, message)
      OsConstants.ELOOP -> FileSystemLoopException(file)
      OsConstants.ENOTDIR -> NotDirectoryException(file)
      OsConstants.ENOTEMPTY -> DirectoryNotEmptyException(file)
      OsConstants.ENOENT -> NoSuchFileException(file, other, message)
      OsConstants.EROFS -> ReadOnlyFileSystemException(file, other, message)
      else -> FileSystemException(file, other, message)
    }.apply { initCause(this@SyscallException) }

  companion object {
    private fun perror(errno: Int, functionName: String): String =
      "$functionName: ${Syscalls.strerror(errno)}"
  }
}
