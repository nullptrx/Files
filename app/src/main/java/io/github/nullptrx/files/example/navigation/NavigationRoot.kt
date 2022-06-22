package io.github.nullptrx.files.example.navigation

import android.content.Context
import java8.nio.file.Path

interface NavigationRoot {
  val path: Path

  fun getName(context: Context): String
}
