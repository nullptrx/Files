package io.github.nullptrx.files.example.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible

class AutoGoneTextView : AppCompatTextView {
  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

  constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
    context, attrs, defStyleAttr
  )

  override fun setText(text: CharSequence?, type: BufferType) {
    super.setText(text, type)

    isVisible = !text.isNullOrEmpty()
  }
}
