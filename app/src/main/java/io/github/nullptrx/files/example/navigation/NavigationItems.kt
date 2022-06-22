package io.github.nullptrx.files.example.navigation

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageVolume
import androidx.annotation.DrawableRes
import androidx.annotation.Size
import io.github.nullptrx.files.compat.getDescriptionCompat
import io.github.nullptrx.files.compat.isPrimaryCompat
import io.github.nullptrx.files.compat.pathCompat
import io.github.nullptrx.files.example.R
import io.github.nullptrx.files.example.settings.Settings
import io.github.nullptrx.files.example.storage.FileSystemRoot
import io.github.nullptrx.files.example.storage.Storage
import io.github.nullptrx.files.extension.isMounted
import io.github.nullptrx.files.extension.valueCompat
import io.github.nullptrx.files.file.JavaFile
import io.github.nullptrx.files.file.asFileSize
import io.github.nullptrx.files.storage.StorageVolumeListLiveData
import java8.nio.file.Path
import java8.nio.file.Paths

val navigationItems: List<NavigationItem?>
  get() =
    mutableListOf<NavigationItem?>().apply {
      addAll(storageItems)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // Starting with R, we can get read/write access to non-primary storage volumes with
        // MANAGE_EXTERNAL_STORAGE. However before R, we only have read-only access to them
        // and need to use the Storage Access Framework instead, so hide them in this case
        // to avoid confusion.
        addAll(storageVolumeItems)
      }
      val standardDirectoryItems = standardDirectoryItems
      if (standardDirectoryItems.isNotEmpty()) {
        add(null)
        addAll(standardDirectoryItems)
      }
      val bookmarkDirectoryItems = bookmarkDirectoryItems
      if (bookmarkDirectoryItems.isNotEmpty()) {
        add(null)
        addAll(bookmarkDirectoryItems)
      }
      add(null)
    }

private val storageItems: List<NavigationItem>
  @Size(min = 0)
  get() = Settings.STORAGES.valueCompat.filter {
    it.isVisible
  }.map { StorageItem(it) }

private abstract class PathItem(val path: Path) : NavigationItem() {
  override fun isChecked(listener: Listener): Boolean = listener.currentPath == path

  override fun onClick(listener: Listener) {
    if (this is NavigationRoot) {
      listener.navigateToRoot(path)
    } else {
      listener.navigateTo(path)
    }
    listener.closeNavigationDrawer()
  }
}

private class StorageItem(
  private val storage: Storage
) : PathItem(storage.path), NavigationRoot {
  init {
    require(storage.isVisible)
  }

  override val id: Long
    get() = storage.id

  override val iconRes: Int
    @DrawableRes
    get() = storage.iconRes

  override fun getTitle(context: Context): String = storage.getName(context)

  override fun getSubtitle(context: Context): String? =
    storage.linuxPath?.let { getStorageSubtitle(it, context) }

  override fun onLongClick(listener: Listener): Boolean {
    listener.onEditStorage(storage)
    return true
  }

  override fun getName(context: Context): String = getTitle(context)
}

private val storageVolumeItems: List<NavigationItem>
  @Size(min = 0)
  get() =
    StorageVolumeListLiveData.valueCompat.filter { !it.isPrimaryCompat && it.isMounted }
      .map { StorageVolumeItem(it) }

private class StorageVolumeItem(
  private val storageVolume: StorageVolume
) : PathItem(Paths.get(storageVolume.pathCompat)), NavigationRoot {
  override val id: Long
    get() = storageVolume.hashCode().toLong()

  override val iconRes: Int
    @DrawableRes
    get() = R.drawable.sd_card_icon_white_24dp

  override fun getTitle(context: Context): String = storageVolume.getDescriptionCompat(context)

  override fun getSubtitle(context: Context): String? =
    getStorageSubtitle(storageVolume.pathCompat, context)

  override fun getName(context: Context): String = getTitle(context)
}

private fun getStorageSubtitle(linuxPath: String, context: Context): String? {
  var totalSpace = JavaFile.getTotalSpace(linuxPath)
  val freeSpace: Long
  when {
    totalSpace != 0L -> freeSpace = JavaFile.getFreeSpace(linuxPath)
    linuxPath == FileSystemRoot.LINUX_PATH -> {
      // Root directory may not be an actual partition on legacy Android versions (can be
      // a ramdisk instead). On modern Android the system partition will be mounted as
      // root instead so let's try with the system partition again.
      // @see https://source.android.com/devices/bootloader/system-as-root
      val systemPath = Environment.getRootDirectory().path
      totalSpace = JavaFile.getTotalSpace(systemPath)
      freeSpace = JavaFile.getFreeSpace(systemPath)
    }
    else -> freeSpace = 0
  }
  if (totalSpace == 0L) {
    return null
  }
  val freeSpaceString = freeSpace.asFileSize().formatHumanReadable(context)
  val totalSpaceString = totalSpace.asFileSize().formatHumanReadable(context)
  return context.getString(
    R.string.navigation_storage_subtitle_format, freeSpaceString, totalSpaceString
  )
}


private val standardDirectoryItems: List<NavigationItem>
  @Size(min = 0)
  get() =
    StandardDirectoriesLiveData.valueCompat
      .filter { it.isEnabled }
      .map { StandardDirectoryItem(it) }

private class StandardDirectoryItem(
  private val standardDirectory: StandardDirectory
) : PathItem(Paths.get(getExternalStorageDirectory(standardDirectory.relativePath))) {
  init {
    require(standardDirectory.isEnabled)
  }

  override val id: Long
    get() = standardDirectory.id

  override val iconRes: Int
    @DrawableRes
    get() = standardDirectory.iconRes

  override fun getTitle(context: Context): String = standardDirectory.getTitle(context)

  override fun onLongClick(listener: Listener): Boolean {
    listener.onEditStandardDirectory(standardDirectory)
    return true
  }
}

val standardDirectories: List<StandardDirectory>
  get() {
    val settingsMap = Settings.STANDARD_DIRECTORY_SETTINGS.valueCompat.associateBy { it.id }
    return defaultStandardDirectories.map {
      val settings = settingsMap[it.key]
      if (settings != null) it.withSettings(settings) else it
    }
  }

private const val relativePathSeparator = ":"

private val defaultStandardDirectories: List<StandardDirectory>
  // HACK: Show QQ, TIM and WeChat standard directories based on whether the directory exists.
  get() =
    DEFAULT_STANDARD_DIRECTORIES.mapNotNull {
      when (it.iconRes) {
        R.drawable.directory_icon_white_24dp -> {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Direct access to Android/data is blocked since Android 11.
            null
          } else {
            for (relativePath in it.relativePath.split(relativePathSeparator)) {
              val path = getExternalStorageDirectory(relativePath)
              if (JavaFile.isDirectory(path)) {
                return@mapNotNull it.copy(relativePath = relativePath)
              }
            }
            null
          }
        }
        else -> it
      }
    }

// @see android.os.Environment#STANDARD_DIRECTORIES
private val DEFAULT_STANDARD_DIRECTORIES = listOf(
  StandardDirectory(
    R.drawable.alarm_icon_white_24dp, R.string.navigation_standard_directory_alarms,
    Environment.DIRECTORY_ALARMS, false
  ),
  StandardDirectory(
    R.drawable.camera_icon_white_24dp, R.string.navigation_standard_directory_dcim,
    Environment.DIRECTORY_DCIM, true
  ),
  StandardDirectory(
    R.drawable.document_icon_white_24dp, R.string.navigation_standard_directory_documents,
    Environment.DIRECTORY_DOCUMENTS, false
  ),
  StandardDirectory(
    R.drawable.download_icon_white_24dp, R.string.navigation_standard_directory_downloads,
    Environment.DIRECTORY_DOWNLOADS, true
  ),
  StandardDirectory(
    R.drawable.video_icon_white_24dp, R.string.navigation_standard_directory_movies,
    Environment.DIRECTORY_MOVIES, true
  ),
  StandardDirectory(
    R.drawable.audio_icon_white_24dp, R.string.navigation_standard_directory_music,
    Environment.DIRECTORY_MUSIC, true
  ),
)

internal fun getExternalStorageDirectory(relativePath: String): String =
  @Suppress("DEPRECATION")
  Environment.getExternalStoragePublicDirectory(relativePath).path

private val bookmarkDirectoryItems: List<NavigationItem>
  @Size(min = 0)
  get() = Settings.BOOKMARK_DIRECTORIES.valueCompat.map { BookmarkDirectoryItem(it) }

private class BookmarkDirectoryItem(
  private val bookmarkDirectory: BookmarkDirectory
) : PathItem(bookmarkDirectory.path) {
  // We cannot simply use super.getId() because different bookmark directories may have
  // the same path.
  override val id: Long
    get() = bookmarkDirectory.id

  @DrawableRes
  override val iconRes: Int = R.drawable.directory_icon_white_24dp

  override fun getTitle(context: Context): String = bookmarkDirectory.name

  override fun onLongClick(listener: Listener): Boolean {
    listener.onEditBookmarkDirectory(bookmarkDirectory)
    return true
  }
}

