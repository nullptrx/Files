package io.github.nullptrx.files.example.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes

class ActivityBarLayout @JvmOverloads constructor(
  context: Context,
  attributeSet: AttributeSet? = null,
  @AttrRes defStyleAttr: Int = 0,
  @StyleRes defStyleRes: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr, defStyleRes) {
  init {
    alpha = 0.96f

    setBackgroundColor(context.resolveThemedColor(android.R.attr.windowBackground))
  }

  override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
    super.dispatchTouchEvent(ev)

    return true
  }
}

fun Context.resolveThemedColor(@AttrRes resId: Int): Int {
  return TypedValue().apply {
    theme.resolveAttribute(resId, this, true)
  }.data
}