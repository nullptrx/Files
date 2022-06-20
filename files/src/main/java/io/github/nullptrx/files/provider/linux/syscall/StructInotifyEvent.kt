package io.github.nullptrx.files.provider.linux.syscall

import io.github.nullptrx.files.provider.common.protobuf.ByteString

class StructInotifyEvent(
  val wd: Int,
  val mask: Int, /* uint32_t */
  val cookie: Int, /* uint32_t */
  val name: ByteString?
)
