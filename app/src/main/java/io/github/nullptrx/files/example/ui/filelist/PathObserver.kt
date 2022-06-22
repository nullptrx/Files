package io.github.nullptrx.files.example.ui.filelist

import androidx.annotation.MainThread
import io.github.nullptrx.files.extension.closeSafe
import io.github.nullptrx.files.provider.common.path.PathObservable
import io.github.nullptrx.files.provider.common.path.observe
import java8.nio.file.Path
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.Closeable
import java.io.IOException

class PathObserver(path: Path, @MainThread onChange: () -> Unit) : Closeable,
  CoroutineScope by MainScope() {
  private var pathObservable: PathObservable? = null

  private var closed = false
  private val lock = Any()

  init {
    launch(Dispatchers.IO) {
      synchronized(lock) {
        if (closed) {
          return@launch
        }
        pathObservable = try {
          path.observe(THROTTLE_INTERVAL_MILLIS)
        } catch (e: UnsupportedOperationException) {
          // Ignored.
          return@launch
        } catch (e: IOException) {
          // Ignored.
          e.printStackTrace()
          return@launch
        }.apply {
          addObserver {
            launch {
              onChange()
            }
          }
        }
      }
    }
  }

  override fun close() {
    launch(Dispatchers.IO) {
      synchronized(lock) {
        if (closed) {
          return@launch
        }
        closed = true
        pathObservable?.closeSafe()
      }
    }
  }

  companion object {
    private const val THROTTLE_INTERVAL_MILLIS = 1000L
  }
}
