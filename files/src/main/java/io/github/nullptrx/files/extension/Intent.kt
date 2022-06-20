package io.github.nullptrx.files.extension

import android.content.Context
import android.content.Intent
import io.github.nullptrx.files.app.application
import kotlin.reflect.KClass

fun <T : Context> KClass<T>.createIntent(): Intent = Intent(application, java)