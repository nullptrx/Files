package io.github.nullptrx.files.extension

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat
import io.github.nullptrx.files.R
import java.util.concurrent.Executor

val Context.mainExecutorCompat: Executor
  get() = ContextCompat.getMainExecutor(this)

fun Context.showToast(textRes: Int, duration: Int = Toast.LENGTH_SHORT) {
  if (Looper.myLooper() != Looper.getMainLooper()) {
    mainExecutorCompat.execute { showToast(textRes, duration) }
    return
  }
  Toast.makeText(this, textRes, duration).show()
}

fun Context.showToast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
  if (Looper.myLooper() != Looper.getMainLooper()) {
    mainExecutorCompat.execute { showToast(text, duration) }
    return
  }
  Toast.makeText(this, text, duration).show()
}

fun Context.startActivitySafe(intent: Intent, options: Bundle? = null) {
  try {
    startActivity(intent, options)
  } catch (e: ActivityNotFoundException) {
    showToast(R.string.activity_not_found)
  }
}