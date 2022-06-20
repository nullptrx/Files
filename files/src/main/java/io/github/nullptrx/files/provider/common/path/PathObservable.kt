package io.github.nullptrx.files.provider.common.path

import java.io.Closeable

interface PathObservable : Closeable {
  fun addObserver(observer: () -> Unit)

  fun removeObserver(observer: () -> Unit)
}
