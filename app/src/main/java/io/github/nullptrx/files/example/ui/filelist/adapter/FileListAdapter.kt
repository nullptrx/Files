package io.github.nullptrx.files.example.ui.filelist.adapter

import android.text.TextUtils
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.dispose
import coil.load
import io.github.nullptrx.files.example.R
import io.github.nullptrx.files.example.coil.AppIconPackageName
import io.github.nullptrx.files.example.databinding.FileItemBinding
import io.github.nullptrx.files.example.extension.layoutInflater
import io.github.nullptrx.files.example.extension.supportsThumbnail
import io.github.nullptrx.files.example.ui.filelist.icon.iconRes
import io.github.nullptrx.files.example.util.CheckableItemBackground
import io.github.nullptrx.files.file.*
import java8.nio.file.Path

class FileListAdapter(private var listener: Listener? = null) :
  AnimatedListAdapter<FileItem, FileListAdapter.ViewHolder>(CALLBACK) {
  companion object {
    private val PAYLOAD_STATE_CHANGED = Any()

    private val CALLBACK = object : DiffUtil.ItemCallback<FileItem>() {
      override fun areItemsTheSame(oldItem: FileItem, newItem: FileItem): Boolean =
        oldItem.path == newItem.path

      override fun areContentsTheSame(oldItem: FileItem, newItem: FileItem): Boolean =
        oldItem == newItem
    }
  }

  private var isSearching = false

  private val selectedFiles = fileItemSetOf()

  private val filePositionMap = mutableMapOf<Path, Int>()

  private lateinit var _comparator: Comparator<FileItem>
  var comparator: Comparator<FileItem>
    get() = _comparator
    set(value) {
      _comparator = value
      if (!isSearching) {
        super.replace(list.sortedWith(value), true)
        rebuildFilePositionMap()
      }
    }

  private var _nameEllipsize: TextUtils.TruncateAt = TextUtils.TruncateAt.END
  var nameEllipsize: TextUtils.TruncateAt
    get() = _nameEllipsize
    set(value) {
      _nameEllipsize = value
      notifyItemRangeChanged(0, itemCount, PAYLOAD_STATE_CHANGED)
    }

  private fun rebuildFilePositionMap() {
    filePositionMap.clear()
    for (index in 0 until itemCount) {
      val file = getItem(index)
      filePositionMap[file.path] = index
    }
  }


  fun replaceSelectedFiles(files: FileItemSet) {
    val changedFiles = fileItemSetOf()
    val iterator = selectedFiles.iterator()
    while (iterator.hasNext()) {
      val file = iterator.next()
      if (file !in files) {
        iterator.remove()
        changedFiles.add(file)
      }
    }
    for (file in files) {
      if (file !in selectedFiles) {
        selectedFiles.add(file)
        changedFiles.add(file)
      }
    }
    for (file in changedFiles) {
      val position = filePositionMap[file.path]
      position?.let { notifyItemChanged(it, PAYLOAD_STATE_CHANGED) }
    }
  }

  // private fun selectFile(file: FileItem) {
  //   if (!isFileSelectable(file)) {
  //     return
  //   }
  //   val selected = file in selectedFiles
  //   if (!selected) {
  //     listener?.clearSelectedFiles()
  //   }
  //   listener?.selectFile(file, !selected)
  // }
  //
  // fun selectAllFiles() {
  //   val files = fileItemSetOf()
  //   for (index in 0 until itemCount) {
  //     val file = getItem(index)
  //     if (isFileSelectable(file)) {
  //       files.add(file)
  //     }
  //   }
  //   listener?.selectFiles(files, true)
  // }

  private fun isFileSelectable(file: FileItem): Boolean {
    return true
  }

  override fun clear() {
    super.clear()

    rebuildFilePositionMap()
  }

  @Deprecated("", ReplaceWith("replaceListAndSearching(list, searching)"))
  override fun replace(list: List<FileItem>, clear: Boolean) {
    throw UnsupportedOperationException()
  }

  fun replaceListAndIsSearching(list: List<FileItem>, isSearching: Boolean) {
    val clear = this.isSearching != isSearching
    this.isSearching = isSearching
    super.replace(if (!isSearching) list.sortedWith(comparator) else list, clear)
    rebuildFilePositionMap()
  }

  interface Listener {
    // fun clearSelectedFiles()
    // fun selectFile(file: FileItem, selected: Boolean)
    // fun selectFiles(files: FileItemSet, selected: Boolean)
    fun openFile(file: FileItem)
    // fun openFileWith(file: FileItem)
    // fun cutFile(file: FileItem)
    // fun copyFile(file: FileItem)
    // fun confirmDeleteFile(file: FileItem)
    // fun showRenameFileDialog(file: FileItem)
    // fun extractFile(file: FileItem)
    // fun showCreateArchiveDialog(file: FileItem)
    // fun shareFile(file: FileItem)
    // fun copyPath(file: FileItem)
    // fun addBookmark(file: FileItem)
    // fun createShortcut(file: FileItem)
    // fun showPropertiesDialog(file: FileItem)
  }


  class ViewHolder(val binding: FileItemBinding) : RecyclerView.ViewHolder(binding.root) {
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(
      FileItemBinding.inflate(parent.context.layoutInflater, parent, false)
    ).apply {
      binding.itemLayout.background =
        CheckableItemBackground.create(binding.itemLayout.context)
      binding.menuButton.setOnClickListener { }
    }
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    throw UnsupportedOperationException()
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
    val file = getItem(position)
    val binding = holder.binding
    val isDirectory = file.attributes.isDirectory
    val enabled = isFileSelectable(file) || isDirectory
    binding.itemLayout.isEnabled = enabled
    binding.menuButton.isEnabled = enabled
    val path = file.path
    val isReadOnly = path.fileSystem.isReadOnly
    val checked = file in selectedFiles
    binding.itemLayout.isChecked = checked
    val nameEllipsize = nameEllipsize
    binding.nameText.ellipsize = nameEllipsize
    binding.nameText.isSelected = nameEllipsize == TextUtils.TruncateAt.MARQUEE
    if (payloads.isNotEmpty()) {
      return
    }
    bindViewHolderAnimation(holder)
    binding.itemLayout.setOnClickListener {
      if (selectedFiles.isEmpty()) {
        listener?.openFile(file)
      } else {
        // selectFile(file)
      }
    }
    binding.itemLayout.setOnLongClickListener {
      if (selectedFiles.isEmpty()) {
        // selectFile(file)
      } else {
        listener?.openFile(file)
      }
      true
    }
    // binding.iconLayout.setOnClickListener { selectFile(file) }
    binding.iconImage.setImageResource(file.mimeType.iconRes)
    binding.iconImage.isVisible = true
    binding.thumbnailImage.dispose()
    binding.thumbnailImage.setImageDrawable(null)
    val supportsThumbnail = file.supportsThumbnail
    binding.thumbnailImage.isVisible = supportsThumbnail
    val attributes = file.attributes
    if (supportsThumbnail) {
      binding.thumbnailImage.load(path to attributes) {
        listener { _, _ -> binding.iconImage.isVisible = false }
      }
    }
    binding.appIconBadgeImage.dispose()
    binding.appIconBadgeImage.setImageDrawable(null)
    val appDirectoryPackageName = file.appDirectoryPackageName
    val hasAppIconBadge = appDirectoryPackageName != null
    binding.appIconBadgeImage.isVisible = hasAppIconBadge
    if (hasAppIconBadge) {
      binding.appIconBadgeImage.load(AppIconPackageName(appDirectoryPackageName!!))
    }
    val badgeIconRes = if (file.attributesNoFollowLinks.isSymbolicLink) {
      if (file.isSymbolicLinkBroken) {
        R.drawable.error_badge_icon_18dp
      } else {
        R.drawable.symbolic_link_badge_icon_18dp
      }
    } else {
      null
    }
    val hasBadge = badgeIconRes != null
    binding.badgeImage.isVisible = hasBadge
    if (hasBadge) {
      binding.badgeImage.setImageResource(badgeIconRes!!)
    }
    binding.nameText.text = file.name
    binding.descriptionText.text = if (isDirectory) {
      null
    } else {
      val context = binding.descriptionText.context
      val lastModificationTime = attributes.lastModifiedTime().toInstant()
        .formatShort(context)
      val size = attributes.fileSize.formatHumanReadable(context)
      val descriptionSeparator = context.getString(R.string.file_item_description_separator)
      listOf(lastModificationTime, size).joinToString(descriptionSeparator)
    }
    // val isArchivePath = path.isArchivePath
  }


}