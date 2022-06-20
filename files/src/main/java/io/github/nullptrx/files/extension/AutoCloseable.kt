package io.github.nullptrx.files.extension

fun AutoCloseable.closeSafe() {
  try {
    close()
  } catch (e: Exception) {
    e.printStackTrace()
  }
}
