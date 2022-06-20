package io.github.nullptrx.files.provider.common.path

import java8.nio.file.DirectoryStream
import java8.nio.file.Path

class PathListDirectoryStream(
  paths: List<Path>,
  filter: DirectoryStream.Filter<in Path>
) : PathIteratorDirectoryStream(paths.iterator(), null, filter)
