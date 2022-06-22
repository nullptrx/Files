package io.github.nullptrx.files.example.ui.filelist

import android.os.Parcelable
import io.github.nullptrx.files.example.extension.reversedCompat
import io.github.nullptrx.files.file.FileItem
import io.github.nullptrx.files.file.extension
import io.github.nullptrx.files.file.name
import kotlinx.parcelize.Parcelize

@Parcelize
data class FileSortOptions(
  val by: By,
  val order: Order,
  val isDirectoriesFirst: Boolean
) : Parcelable {
  fun createComparator(): Comparator<FileItem> {
    var comparator = compareBy<FileItem> {
      NAME_UNIMPORTANT_PREFIXES.any { prefix -> it.name.startsWith(prefix) }
    }.thenBy { it.nameCollationKey }
    when (by) {
      // Nothing to do.
      By.NAME -> {}
      By.TYPE ->
        comparator = compareBy<FileItem, String>(String.CASE_INSENSITIVE_ORDER) {
          it.extension
        }.then(comparator)
      By.SIZE -> comparator = compareBy<FileItem> { it.attributes.size() }.then(comparator)
      By.LAST_MODIFIED ->
        comparator = compareBy<FileItem> { it.attributes.lastModifiedTime() }
          .then(comparator)
    }
    when (order) {
      Order.ASCENDING -> {}
      Order.DESCENDING -> comparator = comparator.reversedCompat()
    }
    if (isDirectoriesFirst) {
      val isDirectoryComparator = compareBy<FileItem> { it.attributes.isDirectory }
        .reversedCompat()
      comparator = isDirectoryComparator.then(comparator)
    }
    return comparator
  }

  companion object {
    // Same behavior as Nautilus.
    private val NAME_UNIMPORTANT_PREFIXES = listOf(".", "#")
  }

  enum class By {
    NAME,
    TYPE,
    SIZE,
    LAST_MODIFIED
  }

  enum class Order {
    ASCENDING,
    DESCENDING
  }
}
