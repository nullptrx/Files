package io.github.nullptrx.files.provider.common.posix

import java.io.IOException

abstract class PosixFileStore : AbstractFileStore() {
  @Throws(IOException::class)
  abstract fun refresh()

  @Throws(IOException::class)
  abstract fun setReadOnly(readOnly: Boolean)
}
