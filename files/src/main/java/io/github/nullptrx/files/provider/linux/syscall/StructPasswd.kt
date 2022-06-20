package io.github.nullptrx.files.provider.linux.syscall

import io.github.nullptrx.files.provider.common.protobuf.ByteString

class StructPasswd(
  val pw_name: ByteString?,
  val pw_uid: Int,
  val pw_gid: Int,
  val pw_gecos: ByteString?,
  val pw_dir: ByteString?,
  val pw_shell: ByteString?
)
