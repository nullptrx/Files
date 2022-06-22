package io.github.nullptrx.files.example.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.AttrRes
import com.google.android.material.R
import com.google.android.material.textfield.TextInputLayout
import io.github.nullptrx.files.example.extension.getDrawableCompat

class ReadOnlyTextInputLayout : TextInputLayout {
  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

  constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(
    context, attrs, defStyleAttr
  )

  init {
    isExpandedHintEnabled = false
  }

  override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
    super.addView(child, index, params)

    if (child is EditText) {
      setDropDown(!child.isTextSelectable)
    }
  }

  fun setDropDown(dropDown: Boolean) {
    if (dropDown) {
      endIconMode = END_ICON_CUSTOM
      endIconDrawable = context.getDrawableCompat(R.drawable.mtrl_ic_arrow_drop_down)
    } else {
      endIconMode = END_ICON_NONE
    }
  }
}
