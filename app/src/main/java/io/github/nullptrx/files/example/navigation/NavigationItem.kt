package io.github.nullptrx.files.example.navigation

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import io.github.nullptrx.files.example.storage.Storage
import java8.nio.file.Path

abstract class NavigationItem {
  abstract val id: Long

  fun getIcon(context: Context): Drawable = AppCompatResources.getDrawable(context, iconRes!!)!!

  @get:DrawableRes
  protected abstract val iconRes: Int?

  abstract fun getTitle(context: Context): String

  open fun getSubtitle(context: Context): String? = null

  open fun isChecked(listener: Listener): Boolean = false

  abstract fun onClick(listener: Listener)

  open fun onLongClick(listener: Listener): Boolean = false

  interface Listener {
    val currentPath: Path
    fun navigateTo(path: Path)
    fun navigateToRoot(path: Path)
    fun onAddStorage()
    fun onEditStorage(storage: Storage)
    fun onEditStandardDirectory(standardDirectory: StandardDirectory)
    fun onEditBookmarkDirectory(bookmarkDirectory: BookmarkDirectory)
    fun closeNavigationDrawer()
    fun startActivity(intent: Intent)
  }
}
