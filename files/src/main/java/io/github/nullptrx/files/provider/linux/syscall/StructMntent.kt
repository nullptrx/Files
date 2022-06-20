package io.github.nullptrx.files.provider.linux.syscall

import android.os.Parcelable
import io.github.nullptrx.files.provider.common.protobuf.ByteString
import kotlinx.parcelize.Parcelize

@Parcelize
class StructMntent(
  val mnt_fsname: ByteString,
  val mnt_dir: ByteString,
  val mnt_type: ByteString,
  val mnt_opts: ByteString,
  val mnt_freq: Int,
  val mnt_passno: Int
) : Parcelable
