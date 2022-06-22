package io.github.nullptrx.files.example.ui.filelist

import io.github.nullptrx.files.example.util.*
import io.github.nullptrx.files.extension.valueCompat
import io.github.nullptrx.files.file.FileItem
import io.github.nullptrx.files.file.loadFileItem
import io.github.nullptrx.files.provider.common.path.search
import java8.nio.file.Path
import kotlinx.coroutines.*
import java.io.IOException

class SearchFileListLiveData(
  private val path: Path,
  private val query: String
) : CloseableLiveData<Stateful<List<FileItem>>>(), CoroutineScope by MainScope() {

  init {
    loadValue()
  }

  fun loadValue() {
    cancel()
    value = Loading(emptyList())
    launch(Dispatchers.IO) {
      val fileList = mutableListOf<FileItem>()
      try {
        path.search(query, INTERVAL_MILLIS) { paths: List<Path> ->
          for (path in paths) {
            val fileItem = try {
              path.loadFileItem()
            } catch (e: IOException) {
              e.printStackTrace()
              // TODO: Support file without information.
              continue
            }
            fileList.add(fileItem)
          }
          postValue(Loading(fileList.toList()))
        }
        postValue(Success(fileList))
      } catch (e: Exception) {
        // TODO: Retrieval of previous value is racy.
        postValue(Failure(valueCompat.value, e))
      }
    }
  }

  override fun close() {
    cancel()
  }

  companion object {
    private const val INTERVAL_MILLIS = 500L
  }
}
