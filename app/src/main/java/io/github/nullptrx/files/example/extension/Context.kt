package io.github.nullptrx.files.example.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.Toast
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.TintTypedArray
import androidx.core.content.ContextCompat
import io.github.nullptrx.files.extension.mainExecutorCompat
import kotlin.OptIn
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

val Context.activity: Activity?
  get() {
    var context = this
    while (true) {
      when (context) {
        is Activity -> return context
        is ContextWrapper -> context = context.baseContext
        else -> return null
      }
    }
  }


fun <T> Context.getSystemServiceCompat(serviceClass: Class<T>): T =
  ContextCompat.getSystemService(this, serviceClass)!!

val Context.layoutInflater: LayoutInflater
  get() = LayoutInflater.from(this)

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

fun Context.getInterpolator(@InterpolatorRes id: Int): Interpolator =
  AnimationUtils.loadInterpolator(this, id)

fun Context.getDimension(@DimenRes id: Int) = resources.getDimension(id)

fun Context.getDimensionPixelOffset(@DimenRes id: Int) = resources.getDimensionPixelOffset(id)

fun Context.getDimensionPixelSize(@DimenRes id: Int) = resources.getDimensionPixelSize(id)

fun Context.getDrawableCompat(@DrawableRes id: Int): Drawable =
  AppCompatResources.getDrawable(this, id)!!

@SuppressLint("RestrictedApi")
fun Context.getColorStateListByAttr(@AttrRes attr: Int): ColorStateList =
  obtainStyledAttributesCompat(attrs = intArrayOf(attr)).use { it.getColorStateList(0) }

@SuppressLint("RestrictedApi")
fun Context.getFloatByAttr(@AttrRes attr: Int): Float =
  obtainStyledAttributesCompat(attrs = intArrayOf(attr)).use { it.getFloat(0, 0f) }

@ColorInt
fun Context.getColorByAttr(@AttrRes attr: Int): Int =
  getColorStateListByAttr(attr).defaultColor

@SuppressLint("RestrictedApi")
fun Context.obtainStyledAttributesCompat(
  set: AttributeSet? = null,
  @StyleableRes attrs: IntArray,
  @AttrRes defStyleAttr: Int = 0,
  @StyleRes defStyleRes: Int = 0
): TintTypedArray =
  TintTypedArray.obtainStyledAttributes(this, set, attrs, defStyleAttr, defStyleRes)


@OptIn(ExperimentalContracts::class)
@SuppressLint("RestrictedApi")
inline fun <R> TintTypedArray.use(block: (TintTypedArray) -> R): R {
  contract {
    callsInPlace(block, InvocationKind.EXACTLY_ONCE)
  }
  return try {
    block(this)
  } finally {
    recycle()
  }
}



