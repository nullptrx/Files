package io.github.nullptrx.files.example.extension

import android.content.res.Resources
import kotlin.math.roundToInt

val Number.dp get() = (toFloat() * Resources.getSystem().displayMetrics.density).roundToInt()