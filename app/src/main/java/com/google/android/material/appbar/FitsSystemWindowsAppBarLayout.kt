package com.google.android.material.appbar

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

open class FitsSystemWindowsAppBarLayout : OnWindowInsetChangedAppBarLayout {
  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

  constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int
  ) : super(context, attrs, defStyleAttr)

  init {
    fitsSystemWindows = true
  }

  override fun onWindowInsetChanged(insets: WindowInsetsCompat): WindowInsetsCompat {
    val windowInsets = insets.toWindowInsets()!!
    updatePadding(
      left = windowInsets.systemWindowInsetLeft, right = windowInsets.systemWindowInsetRight
    )
    return super.onWindowInsetChanged(insets)
  }
}
