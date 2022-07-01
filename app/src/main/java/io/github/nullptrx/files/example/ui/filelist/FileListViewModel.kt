package io.github.nullptrx.files.example.ui.filelist;

import android.os.Parcelable
import androidx.lifecycle.*
import io.github.nullptrx.files.example.ui.filelist.breadcrumb.BreadcrumbData
import io.github.nullptrx.files.example.ui.filelist.breadcrumb.BreadcrumbLiveData
import io.github.nullptrx.files.example.ui.filelist.trail.TrailLiveData
import io.github.nullptrx.files.example.util.CloseableLiveData
import io.github.nullptrx.files.example.util.Stateful
import io.github.nullptrx.files.extension.valueCompat
import io.github.nullptrx.files.file.FileItem
import io.github.nullptrx.files.provider.archive.isArchivePath
import io.github.nullptrx.files.provider.common.path.archiveRefresh
import java8.nio.file.Path
import java.io.Closeable

class FileListViewModel : ViewModel() {
  private val trailLiveData = TrailLiveData()
  val hasTrail: Boolean
    get() = trailLiveData.value != null
  val pendingState: Parcelable?
    get() = trailLiveData.valueCompat.pendingState

  val breadcrumbLiveData: LiveData<BreadcrumbData> = BreadcrumbLiveData(trailLiveData)

  fun navigateTo(lastState: Parcelable, path: Path) = trailLiveData.navigateTo(lastState, path)

  fun resetTo(path: Path) = trailLiveData.resetTo(path)

  fun navigateUp(overrideBreadcrumb: Boolean): Boolean =
    if (!overrideBreadcrumb && breadcrumbLiveData.valueCompat.selectedIndex == 0) {
      false
    } else {
      trailLiveData.navigateUp()
    }

  val currentPathLiveData = trailLiveData.map { it.currentPath }
  val currentPath: Path
    get() = currentPathLiveData.valueCompat

  private val _searchStateLiveData = MutableLiveData(SearchState(false, ""))
  val searchStateLiveData: LiveData<SearchState> = _searchStateLiveData
  val searchState: SearchState
    get() = _searchStateLiveData.valueCompat

  private val _isRequestingStorageAccessLiveData = MutableLiveData(false)
  var isStorageAccessRequested: Boolean
    get() = _isRequestingStorageAccessLiveData.valueCompat
    set(value) {
      _isRequestingStorageAccessLiveData.value = value
    }

  override fun onCleared() {
    _fileListLiveData.close()
  }


  fun search(query: String) {
    val searchState = _searchStateLiveData.valueCompat
    if (searchState.isSearching && searchState.query == query) {
      return
    }
    _searchStateLiveData.value = SearchState(true, query)
  }

  fun stopSearching() {
    val searchState = _searchStateLiveData.valueCompat
    if (!searchState.isSearching) {
      return
    }
    _searchStateLiveData.value = SearchState(false, "")
  }

  private val _fileListLiveData =
    FileListSwitchMapLiveData(currentPathLiveData, _searchStateLiveData)
  val fileListLiveData: LiveData<Stateful<List<FileItem>>>
    get() = _fileListLiveData
  val fileListStateful: Stateful<List<FileItem>>
    get() = _fileListLiveData.valueCompat

  private val _sortOptionsLiveData = FileSortOptionsLiveData(currentPathLiveData)
  val sortOptionsLiveData: LiveData<FileSortOptions> = _sortOptionsLiveData
  val sortOptions: FileSortOptions
    get() = _sortOptionsLiveData.valueCompat

  fun setSortBy(by: FileSortOptions.By) = _sortOptionsLiveData.putBy(by)

  fun setSortOrder(order: FileSortOptions.Order) = _sortOptionsLiveData.putOrder(order)

  fun setSortDirectoriesFirst(isDirectoriesFirst: Boolean) =
    _sortOptionsLiveData.putIsDirectoriesFirst(isDirectoriesFirst)

  fun reload() {
    val path = currentPath
    if (path.isArchivePath) {
      path.archiveRefresh()
    }
    _fileListLiveData.reload()
  }

  val searchViewExpandedLiveData = MutableLiveData(false)
  var isSearchViewExpanded: Boolean
    get() = searchViewExpandedLiveData.valueCompat
    set(value) {
      if (searchViewExpandedLiveData.valueCompat == value) {
        return
      }
      searchViewExpandedLiveData.value = value
    }

  private val _searchViewQueryLiveData = MutableLiveData("")
  var searchViewQuery: String
    get() = _searchViewQueryLiveData.valueCompat
    set(value) {
      if (_searchViewQueryLiveData.valueCompat == value) {
        return
      }
      _searchViewQueryLiveData.value = value
    }


  private class FileListSwitchMapLiveData(
    private val pathLiveData: LiveData<Path>,
    private val searchStateLiveData: LiveData<SearchState>
  ) : MediatorLiveData<Stateful<List<FileItem>>>(), Closeable {
    private var liveData: CloseableLiveData<Stateful<List<FileItem>>>? = null

    init {
      addSource(pathLiveData) { updateSource() }
      addSource(searchStateLiveData) { updateSource() }
    }

    private fun updateSource() {
      liveData?.let {
        removeSource(it)
        it.close()
      }
      val path = pathLiveData.valueCompat
      val searchState = searchStateLiveData.valueCompat
      val liveData = if (searchState.isSearching) {
        SearchFileListLiveData(path, searchState.query)
      } else {
        FileListLiveData(path)
      }
      this.liveData = liveData
      addSource(liveData) { value = it }
    }

    fun reload() {
      when (val liveData = liveData) {
        is FileListLiveData -> liveData.loadValue()
        is SearchFileListLiveData -> liveData.loadValue()
      }
    }

    override fun close() {
      liveData?.let {
        removeSource(it)
        it.close()
        this.liveData = null
      }
    }
  }
}