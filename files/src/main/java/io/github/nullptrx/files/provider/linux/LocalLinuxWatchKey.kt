package io.github.nullptrx.files.provider.linux

import io.github.nullptrx.files.provider.common.watch.AbstractWatchKey


internal class LocalLinuxWatchKey(
  watchService: LocalLinuxWatchService,
  path: LinuxPath,
  val watchDescriptor: Int
) : AbstractWatchKey<LocalLinuxWatchKey, LinuxPath>(watchService, path)
