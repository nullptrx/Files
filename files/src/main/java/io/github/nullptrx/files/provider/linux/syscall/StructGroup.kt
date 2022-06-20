package io.github.nullptrx.files.provider.linux.syscall

import io.github.nullptrx.files.provider.common.protobuf.ByteString

class StructGroup(
  val gr_name: ByteString?,
  val gr_passwd: ByteString?,
  val gr_gid: Int,
  val gr_mem: Array<ByteString>?
)
