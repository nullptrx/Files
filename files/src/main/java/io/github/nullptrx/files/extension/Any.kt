package io.github.nullptrx.files.extension

fun Any.hash(vararg values: Any?): Int = values.contentDeepHashCode()