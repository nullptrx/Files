package io.github.nullptrx.files.example.widget

import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import io.github.nullptrx.files.example.extension.fadeInUnsafe
import io.github.nullptrx.files.example.extension.fadeOutUnsafe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class OverlayToolbarActionMode(bar: ViewGroup, toolbar: Toolbar) : ToolbarActionMode(bar, toolbar),
  CoroutineScope by MainScope() {
  constructor(toolbar: Toolbar) : this(toolbar, toolbar)

  init {
    bar.isVisible = false
  }

  override fun show(bar: ViewGroup, animate: Boolean) {
    if (animate) {
      launch {

        bar.fadeInUnsafe()
      }
    } else {
      bar.isVisible = true
    }
  }

  override fun hide(bar: ViewGroup, animate: Boolean) {
    if (animate) {
      bar.fadeOutUnsafe()
    } else {
      bar.isVisible = false
    }
  }
}
