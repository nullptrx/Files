package io.github.nullptrx.files.example.extension

import android.animation.ValueAnimator
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout

private class AppBarElevationController(
  private val activityBar: AppBarLayout
) {
  private var animator: ValueAnimator? = null

  var elevated: Boolean = false
    set(value) {
      if (field == value)
        return

      field = value

      animator?.end()

      animator = if (value) {
        ValueAnimator.ofFloat(
          activityBar.elevation,
          5.dp.toFloat()
        )
      } else {
        ValueAnimator.ofFloat(
          activityBar.elevation,
          0f
        )
      }.apply {
        addUpdateListener {
          activityBar.elevation = it.animatedValue as Float
        }

        start()
      }
    }
}

fun RecyclerView.bindAppBarElevation(activityBar: AppBarLayout) {
  addOnScrollListener(object : RecyclerView.OnScrollListener() {
    private val controller = AppBarElevationController(activityBar)

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
      controller.elevated = !recyclerView.isTop
    }
  })
}