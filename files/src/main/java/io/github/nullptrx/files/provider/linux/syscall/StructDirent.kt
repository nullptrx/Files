package io.github.nullptrx.files.provider.linux.syscall

import io.github.nullptrx.files.provider.common.protobuf.ByteString

class StructDirent(
  val d_ino: Long, /*ino_t*/
  val d_off: Long, /*off64_t*/
  val d_reclen: Int, /*unsigned short*/
  val d_type: Int, /*unsigned char*/
  val d_name: ByteString
)
