package io.github.nullptrx.files.extension

fun <T : CharSequence> T.takeIfNotBlank(): T? = ifBlank { null }

fun <T : CharSequence> T.takeIfNotEmpty(): T? = ifEmpty { null }