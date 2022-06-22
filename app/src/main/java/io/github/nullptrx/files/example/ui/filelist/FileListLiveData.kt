package io.github.nullptrx.files.example.ui.filelist

import io.github.nullptrx.files.example.util.*
import io.github.nullptrx.files.extension.valueCompat
import io.github.nullptrx.files.file.FileItem
import io.github.nullptrx.files.file.loadFileItem
import io.github.nullptrx.files.provider.common.path.newDirectoryStream
import java8.nio.file.DirectoryIteratorException
import java8.nio.file.Path
import kotlinx.coroutines.*
import java.io.IOException

class FileListLiveData(private val path: Path) : CloseableLiveData<Stateful<List<FileItem>>>(),
  CoroutineScope by MainScope() {

  private var job: Job? = null

  private val observer: PathObserver

  @Volatile
  private var isChangedWhileInactive = false

  init {
    loadValue()
    observer = PathObserver(path) { onChangeObserved() }
  }

  fun loadValue() {
    value = Loading(value?.value)
    job?.cancel()
    job = launch(Dispatchers.IO) {

      // try {
      //   path.newDirectoryStream().use { directoryStream ->
      //     val fileList = mutableListOf<FileItem>()
      //     for (path in directoryStream) {
      //       try {
      //         fileList.add(path.loadFileItem())
      //       } catch (e: DirectoryIteratorException) {
      //         // TODO: Ignoring such a file can be misleading and we need to support
      //         //  files without information.
      //         e.printStackTrace()
      //       } catch (e: IOException) {
      //         e.printStackTrace()
      //       }
      //     }
      //     postValue(Success(fileList))
      //   }
      // } catch (e: Exception) {
      //   postValue(Failure(valueCompat.value, e))
      // }

      runCatching {
        path.newDirectoryStream().use { directoryStream ->
          val fileList = mutableListOf<FileItem>()
          for (path in directoryStream) {
            try {
              fileList.add(path.loadFileItem())
            } catch (e: DirectoryIteratorException) {
              // TODO: Ignoring such a file can be misleading and we need to support
              //  files without information.
              e.printStackTrace()
            } catch (e: IOException) {
              e.printStackTrace()
            }
          }
          fileList
        }
      }.onSuccess {
        val value = Success(it as List<FileItem>)
        postValue(value)
      }.onFailure {
        val value = Failure(valueCompat.value, it)
        postValue(value)
      }
    }
  }

  private fun onChangeObserved() {
    if (hasActiveObservers()) {
      loadValue()
    } else {
      isChangedWhileInactive = true
    }
  }

  override fun onActive() {
    if (isChangedWhileInactive) {
      loadValue()
      isChangedWhileInactive = false
    }
  }

  override fun close() {
    observer.close()
    job?.cancel()
  }
}
