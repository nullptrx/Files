package io.github.nullptrx.files.example.ui.filelist.breadcrumb

import android.content.Context
import java8.nio.file.Path

data class BreadcrumbData(
  val paths: List<Path>,
  val nameProducers: List<(Context) -> String>,
  val selectedIndex: Int
)
