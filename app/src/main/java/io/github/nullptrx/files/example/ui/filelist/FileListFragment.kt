package io.github.nullptrx.files.example.ui.filelist

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.view.updatePaddingRelative
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.appbar.AppBarLayout
import io.github.nullptrx.files.example.R
import io.github.nullptrx.files.example.app.application
import io.github.nullptrx.files.example.databinding.FileListFragmentAppBarIncludeBinding
import io.github.nullptrx.files.example.databinding.FileListFragmentBinding
import io.github.nullptrx.files.example.databinding.FileListFragmentBottomBarIncludeBinding
import io.github.nullptrx.files.example.databinding.FileListFragmentContentIncludeBinding
import io.github.nullptrx.files.example.dialog.ShowRequestStoragePermissionInSettingsRationaleDialogFragment
import io.github.nullptrx.files.example.extension.*
import io.github.nullptrx.files.example.ui.filelist.adapter.FileListAdapter
import io.github.nullptrx.files.example.util.*
import io.github.nullptrx.files.example.widget.*
import io.github.nullptrx.files.extension.toUserFriendlyString
import io.github.nullptrx.files.extension.valueCompat
import io.github.nullptrx.files.file.FileItem
import io.github.nullptrx.files.file.isListable
import io.github.nullptrx.files.file.listablePath
import io.github.nullptrx.files.provider.common.path.option.ParcelableArgs
import io.github.nullptrx.files.provider.common.path.option.args
import io.github.nullptrx.files.settings.Settings
import java8.nio.file.Path
import kotlinx.parcelize.Parcelize

class FileListFragment : Fragment(), ShowRequestStoragePermissionRationaleDialogFragment.Listener,
  ShowRequestAllFilesAccessRationaleDialogFragment.Listener,
  ShowRequestStoragePermissionInSettingsRationaleDialogFragment.Listener,
  BreadcrumbLayout.Listener, FileListAdapter.Listener {

  private val args by args<Args>()
  private val argsPath by lazy { args.intent.extraPath }

  private val viewModel by viewModels { { FileListViewModel() } }

  private lateinit var binding: Binding

  private lateinit var overlayActionMode: ToolbarActionMode

  private lateinit var bottomActionMode: ToolbarActionMode

  private lateinit var adapter: FileListAdapter

  @Parcelize
  class Args(val intent: Intent) : ParcelableArgs

  private val requestAllFilesAccessLauncher = registerForActivityResult(
    RequestAllFilesAccessContract(), this::onRequestAllFilesAccessResult
  )
  private val requestStoragePermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission(), this::onRequestStoragePermissionResult
  )
  private val requestStoragePermissionInSettingsLauncher = registerForActivityResult(
    RequestStoragePermissionInSettingsContract(),
    this::onRequestStoragePermissionInSettingsResult
  )

  private val debouncedSearchRunnable = DebouncedRunnable(Handler(Looper.getMainLooper()), 1000) {
    if (!isResumed || !viewModel.isSearchViewExpanded) {
      return@DebouncedRunnable
    }
    val query = viewModel.searchViewQuery
    if (query.isEmpty()) {
      return@DebouncedRunnable
    }
    viewModel.search(query)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View =
    Binding.inflate(inflater, container, false)
      .also { binding = it }
      .root

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    overlayActionMode = OverlayToolbarActionMode(binding.overlayToolbar)
    bottomActionMode = PersistentBarLayoutToolbarActionMode(
      binding.persistentBarLayout, binding.bottomBarLayout, binding.bottomToolbar
    )
    // TODO: breadcrumb
    binding.breadcrumbLayout.setListener(this)
    // TODO: refresh
    binding.swipeRefreshLayout.setOnRefreshListener { this.refresh() }
    val contentLayoutInitialPaddingBottom = binding.contentLayout.paddingBottom
    binding.appBarLayout.addOnOffsetChangedListener(
      AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
        binding.contentLayout.updatePaddingRelative(
          bottom = contentLayoutInitialPaddingBottom +
              binding.appBarLayout.totalScrollRange + verticalOffset
        )
      }
    )
    // binding.appBarLayout.syncBackgroundElevationTo(binding.overlayToolbar)
    // binding.recyclerView.bindAppBarElevation(binding.appBarLayout)

    binding.recyclerView.layoutManager = GridLayoutManager(activity, /* TODO */ 1)
    adapter = FileListAdapter(this)
    binding.recyclerView.adapter = adapter

    if (!viewModel.hasTrail) {
      var path = argsPath
      val intent = args.intent
      if (path == null) {
        path = Settings.FILE_LIST_DEFAULT_DIRECTORY.valueCompat
      }
      viewModel.resetTo(path)

    }

    // viewModel.currentPathLiveData.observe(viewLifecycleOwner) { onCurrentPathChanged(it) }
    // viewModel.searchViewExpandedLiveData.observe(viewLifecycleOwner) {
    //   onSearchViewExpandedChanged(it)
    // }

    // viewModel.currentPathLiveData.observe(viewLifecycleOwner) { onCurrentPathChanged(it) }
    viewModel.breadcrumbLiveData.observe(viewLifecycleOwner) {
      binding.breadcrumbLayout.setData(it)
    }
    viewModel.sortOptionsLiveData.observe(viewLifecycleOwner) { onSortOptionsChanged(it) }
    viewModel.fileListLiveData.observe(viewLifecycleOwner) { onFileListChanged(it) }

  }

  override fun onResume() {
    super.onResume()

    ensureStorageAccess()
  }

  fun onBackPressed(): Boolean {
    return false
  }

  private fun ensureStorageAccess() {
    if (viewModel.isStorageAccessRequested) {
      return
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      if (!Environment.isExternalStorageManager()) {
        ShowRequestAllFilesAccessRationaleDialogFragment.show(this)
        viewModel.isStorageAccessRequested = true
      }
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED
      ) {
        if (shouldShowRequestPermissionRationale(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
          )
        ) {
          ShowRequestStoragePermissionRationaleDialogFragment.show(this)
        } else {
          requestStoragePermission()
        }
        viewModel.isStorageAccessRequested = true
      }
    }
  }

  override fun requestStoragePermission() {
    requestStoragePermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
  }

  override fun requestAllFilesAccess() {
    requestAllFilesAccessLauncher.launch(Unit)
  }

  override fun requestStoragePermissionInSettings() {
    requestStoragePermissionInSettingsLauncher.launch(Unit)
  }

  private fun onSortOptionsChanged(sortOptions: FileSortOptions) {
    adapter.comparator = sortOptions.createComparator()
    // updateSortMenuItems()
  }

  private fun onFileListChanged(stateful: Stateful<List<FileItem>>) {
    val files = stateful.value
    val isSearching = viewModel.searchState.isSearching
    when {
      stateful is Failure -> binding.toolbar.setSubtitle("错误")
      stateful is Loading && !isSearching -> binding.toolbar.setSubtitle("加载中")
      else -> binding.toolbar.subtitle = getSubtitle(files!!)
    }
    val hasFiles = !files.isNullOrEmpty()
    binding.swipeRefreshLayout.isRefreshing = stateful is Loading && (hasFiles || isSearching)
    binding.progress.fadeToVisibilityUnsafe(stateful is Loading && !(hasFiles || isSearching))
    binding.errorText.fadeToVisibilityUnsafe(stateful is Failure && !hasFiles)
    val throwable = (stateful as? Failure)?.throwable
    if (throwable != null) {
      throwable.printStackTrace()
      val error = throwable.toString()
      if (hasFiles) {
        showToast(error)
      } else {
        binding.errorText.text = "无权限访问"
      }
    }
    binding.emptyView.fadeToVisibilityUnsafe(stateful is Success && !hasFiles)
    if (files != null) {
      updateAdapterFileList()
    } else {
      // This resets animation as well.
      adapter.clear()
    }
    if (stateful is Success) {
      viewModel.pendingState
        ?.let { binding.recyclerView.layoutManager!!.onRestoreInstanceState(it) }
    }
  }

  private fun getSubtitle(files: List<FileItem>): String {
    val directoryCount = files.count { it.attributes.isDirectory }
    val fileCount = files.size - directoryCount
    val directoryCountText = if (directoryCount > 0) {
      getQuantityString(
        R.plurals.file_list_subtitle_directory_count_format, directoryCount, directoryCount
      )
    } else {
      null
    }
    val fileCountText = if (fileCount > 0) {
      getQuantityString(
        R.plurals.file_list_subtitle_file_count_format, fileCount, fileCount
      )
    } else {
      null
    }
    return when {
      !directoryCountText.isNullOrEmpty() && !fileCountText.isNullOrEmpty() ->
        (directoryCountText + getString(R.string.file_list_subtitle_separator)
            + fileCountText)
      !directoryCountText.isNullOrEmpty() -> directoryCountText
      !fileCountText.isNullOrEmpty() -> fileCountText
      else -> ""
    }
  }

  private fun updateAdapterFileList() {
    var files = viewModel.fileListStateful.value ?: return
    // if (!Settings.FILE_LIST_SHOW_HIDDEN_FILES.valueCompat) {
    //   files = files.filterNot { it.isHidden }
    // }
    adapter.replaceListAndIsSearching(files, viewModel.searchState.isSearching)
  }


  private class RequestAllFilesAccessContract : ActivityResultContract<Unit, Boolean>() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun createIntent(context: Context, input: Unit): Intent =
      Intent(
        android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
        Uri.fromParts("package", context.packageName, null)
      )

    @RequiresApi(Build.VERSION_CODES.R)
    override fun parseResult(resultCode: Int, intent: Intent?): Boolean =
      Environment.isExternalStorageManager()
  }

  private class RequestStoragePermissionInSettingsContract
    : ActivityResultContract<Unit, Boolean>() {
    override fun createIntent(context: Context, input: Unit): Intent =
      Intent(
        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
      )

    override fun parseResult(resultCode: Int, intent: Intent?): Boolean =
      application.checkSelfPermissionCompat(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
      ) == PackageManager.PERMISSION_GRANTED
  }

  private fun refresh() {
    viewModel.reload()
  }

  private fun onRequestAllFilesAccessResult(isGranted: Boolean) {
    if (isGranted) {
      viewModel.isStorageAccessRequested = false
      refresh()
    }
  }

  private fun onRequestStoragePermissionResult(isGranted: Boolean) {
    if (isGranted) {
      viewModel.isStorageAccessRequested = false
      refresh()
    } else if (!shouldShowRequestPermissionRationale(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
      )
    ) {
      ShowRequestStoragePermissionInSettingsRationaleDialogFragment.show(this)
    }
  }

  private fun onRequestStoragePermissionInSettingsResult(isGranted: Boolean) {
    if (isGranted) {
      viewModel.isStorageAccessRequested = false
      refresh()
    }
  }

  private class Binding private constructor(
    val root: View,
    val persistentBarLayout: PersistentBarLayout,
    val appBarLayout: AppBarLayout,
    val toolbar: Toolbar,
    val overlayToolbar: Toolbar,
    val breadcrumbLayout: BreadcrumbLayout,
    val contentLayout: ViewGroup,
    val progress: ProgressBar,
    val errorText: TextView,
    val emptyView: View,
    val swipeRefreshLayout: SwipeRefreshLayout,
    val recyclerView: RecyclerView,
    val bottomBarLayout: ViewGroup,
    val bottomToolbar: Toolbar,
  ) {
    companion object {
      fun inflate(
        inflater: LayoutInflater,
        root: ViewGroup?,
        attachToRoot: Boolean
      ): Binding {
        val binding = FileListFragmentBinding.inflate(inflater, root, attachToRoot)
        val bindingRoot = binding.root
        val appBarBinding = FileListFragmentAppBarIncludeBinding.bind(bindingRoot)
        val contentBinding = FileListFragmentContentIncludeBinding.bind(bindingRoot)
        val bottomBarBinding = FileListFragmentBottomBarIncludeBinding.bind(bindingRoot)
        return Binding(
          bindingRoot,
          binding.persistentBarLayout, appBarBinding.appBarLayout,
          appBarBinding.toolbar, appBarBinding.overlayToolbar,
          appBarBinding.breadcrumbLayout, contentBinding.contentLayout,
          contentBinding.progress, contentBinding.errorText, contentBinding.emptyView,
          contentBinding.swipeRefreshLayout, contentBinding.recyclerView,
          bottomBarBinding.bottomBarLayout, bottomBarBinding.bottomToolbar,
        )
      }
    }
  }

  override fun navigateTo(path: Path) {
    val state = binding.recyclerView.layoutManager!!.onSaveInstanceState()
    viewModel.navigateTo(state!!, path)
  }

  val clipboardManager: ClipboardManager by lazy {
    application.getSystemServiceCompat(ClipboardManager::class.java)
  }

  override fun copyPath(path: Path) {
    clipboardManager.copyText(path.toUserFriendlyString(), requireContext())
  }

  override fun openFile(file: FileItem) {
    if (file.isListable) {
      navigateTo(file.listablePath)
      return
    }
  }
}