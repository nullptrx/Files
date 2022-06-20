package io.github.nullptrx.files.provider.common.path.option

import android.system.OsConstants
import io.github.nullptrx.files.provider.linux.syscall.Constants
import java8.nio.file.LinkOption
import java8.nio.file.OpenOption
import java8.nio.file.StandardOpenOption

class OpenOptions(
  val read: Boolean,
  val write: Boolean,
  val append: Boolean,
  val truncateExisting: Boolean,
  val create: Boolean,
  val createNew: Boolean,
  val deleteOnClose: Boolean,
  val sparse: Boolean,
  val sync: Boolean,
  val dsync: Boolean,
  val noFollowLinks: Boolean
)

fun Array<out OpenOption>.toOpenOptions(): OpenOptions = setOf(*this).toOpenOptions()

fun Set<OpenOption>.toOpenOptions(): OpenOptions {
  var read = false
  var write = false
  var append = false
  var truncateExisting = false
  var create = false
  var createNew = false
  var deleteOnClose = false
  var sparse = false
  var sync = false
  var dsync = false
  var noFollowLinks = false
  for (option in this) {
    when (option) {
      is StandardOpenOption -> when (option) {
        StandardOpenOption.READ -> read = true
        StandardOpenOption.WRITE -> write = true
        StandardOpenOption.APPEND -> append = true
        StandardOpenOption.TRUNCATE_EXISTING -> truncateExisting = true
        StandardOpenOption.CREATE -> create = true
        StandardOpenOption.CREATE_NEW -> createNew = true
        StandardOpenOption.DELETE_ON_CLOSE -> deleteOnClose = true
        StandardOpenOption.SPARSE -> sparse = true
        StandardOpenOption.SYNC -> sync = true
        StandardOpenOption.DSYNC -> dsync = true
        else -> throw UnsupportedOperationException(option.toString())
      }
      LinkOption.NOFOLLOW_LINKS -> noFollowLinks = true
      else -> throw UnsupportedOperationException(option.toString())
    }
  }
  if (!read && !write) {
    if (append) {
      write = true
    } else {
      read = true
    }
  }
  if (deleteOnClose) {
    noFollowLinks = true
  }
  check(!(read && append)) { "${StandardOpenOption.READ} + ${StandardOpenOption.APPEND}" }
  check(!(append && truncateExisting)) {
    "${StandardOpenOption.APPEND} + ${StandardOpenOption.TRUNCATE_EXISTING}"
  }
  if (!write) {
    append = false
    truncateExisting = false
    create = false
    createNew = false
  }
  return OpenOptions(
    read, write, append, truncateExisting, create, createNew, deleteOnClose, sparse,
    sync, dsync, noFollowLinks
  )
}

internal fun OpenOptions.toLinuxFlags(): Int {
  var flags = if (read && write) {
    OsConstants.O_RDWR
  } else if (write) {
    OsConstants.O_WRONLY
  } else {
    OsConstants.O_RDONLY
  }
  if (append) {
    flags = flags or OsConstants.O_APPEND
  }
  if (truncateExisting) {
    flags = flags or OsConstants.O_TRUNC
  }
  if (createNew) {
    flags = flags or OsConstants.O_CREAT or OsConstants.O_EXCL
  } else if (create) {
    flags = flags or OsConstants.O_CREAT
  }
  if (sync) {
    flags = flags or OsConstants.O_SYNC
  }
  if (dsync) {
    flags = flags or Constants.O_DSYNC
  }
  if (noFollowLinks || (!createNew && deleteOnClose)) {
    flags = flags or OsConstants.O_NOFOLLOW
  }
  return flags
}
