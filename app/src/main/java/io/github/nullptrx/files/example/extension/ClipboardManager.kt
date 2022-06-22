package io.github.nullptrx.files.example.extension

import android.content.ClipboardManager
import android.content.Context
import io.github.nullptrx.files.app.application
import io.github.nullptrx.files.example.R

import android.content.ClipData
import kotlin.reflect.KClass

fun ClipData.firstOrNull(): ClipData.Item? = if (itemCount > 0) getItemAt(0) else null

fun KClass<ClipData>.create(
  label: CharSequence?,
  mimeTypes: List<String>,
  items: List<ClipData.Item>
): ClipData =
  ClipData(label, mimeTypes.toTypedArray(), items[0])
    .apply { items.asSequence().drop(1).forEach { addItem(it) } }


var ClipboardManager.primaryText: CharSequence
  get() = primaryClip?.firstOrNull()?.coerceToText(application)!!
  set(value) {
    setPrimaryClip(ClipData.newPlainText(null, value))
  }

private const val TOAST_COPIED_TEXT_MAX_LENGTH = 40

fun ClipboardManager.copyText(text: CharSequence, context: Context) {
  primaryText = text
  var copiedText = text
  var ellipsized = false
  if (copiedText.length > TOAST_COPIED_TEXT_MAX_LENGTH) {
    copiedText = copiedText.subSequence(0, TOAST_COPIED_TEXT_MAX_LENGTH)
    ellipsized = true
  }
  val indexOfFirstNewline = copiedText.indexOf('\n')
  if (indexOfFirstNewline != -1) {
    val indexOfSecondNewline = copiedText.indexOf('\n', indexOfFirstNewline + 1)
    if (indexOfSecondNewline != -1) {
      copiedText = copiedText.subSequence(0, indexOfSecondNewline)
      ellipsized = true
    }
  }
  if (ellipsized) {
    copiedText = "$copiedText…"
  }
  context.showToast(context.getString(R.string.copied_to_clipboard_format, copiedText))
}
