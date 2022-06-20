package io.github.nullptrx.files.provider.common

import java.io.IOException

class RemoteFileSystemException : IOException {
  constructor()

  constructor(message: String?) : super(message)

  constructor(message: String?, cause: Throwable?) : super(message, cause)

  constructor(cause: Throwable?) : super(cause)
}
