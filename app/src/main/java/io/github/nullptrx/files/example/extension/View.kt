package io.github.nullptrx.files.example.extension

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

suspend fun View.fadeIn(force: Boolean = false) {
  if (!isVisible) {
    alpha = 0f
    isVisible = true
  }
  animate().run {
    alpha(1f)
    if (!(isLaidOut || force) || (isVisible && alpha == 1f)) {
      duration = 0
    } else {
      duration = 200
      interpolator = context.getInterpolator(android.R.interpolator.fast_out_slow_in)
    }
    start()
    awaitEnd()
  }
}

@OptIn(DelicateCoroutinesApi::class)
fun View.fadeInUnsafe(force: Boolean = false) {
  GlobalScope.launch(Dispatchers.Main.immediate) { fadeIn(force) }
}

suspend fun View.fadeOut(force: Boolean = false, gone: Boolean = false) {
  animate().run {
    alpha(0f)
    if (!(isLaidOut || force) || (!isVisible || alpha == 0f)) {
      duration = 0
    } else {
      duration = 200
      interpolator = context.getInterpolator(android.R.interpolator.fast_out_linear_in)
    }
    start()
    awaitEnd()
  }
  if (gone) {
    isGone = true
  } else {
    isInvisible = true
  }
}

@OptIn(DelicateCoroutinesApi::class)
fun View.fadeOutUnsafe(force: Boolean = false, gone: Boolean = false) {
  GlobalScope.launch(Dispatchers.Main.immediate) { fadeOut(force, gone) }
}

suspend fun View.fadeToVisibility(visible: Boolean, force: Boolean = false, gone: Boolean = false) {
  if (visible) {
    fadeIn(force)
  } else {
    fadeOut(force, gone)
  }
}

@OptIn(DelicateCoroutinesApi::class)
fun View.fadeToVisibilityUnsafe(visible: Boolean, force: Boolean = false, gone: Boolean = false) {
  GlobalScope.launch(Dispatchers.Main.immediate) { fadeToVisibility(visible, force, gone) }
}