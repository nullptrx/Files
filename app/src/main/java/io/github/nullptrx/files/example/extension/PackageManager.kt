package io.github.nullptrx.files.example.extension

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.SigningInfo
import android.os.Build
import io.github.nullptrx.files.extension.andInv
import io.github.nullptrx.files.extension.hasBits
import io.github.nullptrx.files.provider.document.isDocumentPath
import io.github.nullptrx.files.provider.document.resolver.DocumentResolver
import io.github.nullptrx.files.provider.linux.isLinuxPath
import java8.nio.file.Path
import java.io.Closeable

object PackageManagerCompat {
  @SuppressLint("InlinedApi")
  const val MATCH_UNINSTALLED_PACKAGES = PackageManager.MATCH_UNINSTALLED_PACKAGES
}

val Path.isGetPackageArchiveInfoCompatible: Boolean
  get() = isLinuxPath || isDocumentPath

fun PackageManager.getPackageArchiveInfoCompat(
  path: Path,
  flags: Int
): Pair<PackageInfo?, Closeable?> {
  val archiveFilePath: String
  val closeable: Closeable?
  when {
    path.isLinuxPath -> {
      archiveFilePath = path.toFile().path
      closeable = null
    }
    path.isDocumentPath -> {
      val pfd = DocumentResolver.openParcelFileDescriptor(path as DocumentResolver.Path, "r")
      archiveFilePath = "/proc/self/fd/${pfd.fd}"
      closeable = pfd
    }
    else -> throw IllegalArgumentException(path.toString())
  }
  var successful = false
  val packageInfo: PackageInfo?
  try {
    packageInfo = getPackageArchiveInfoCompat(archiveFilePath, flags)?.apply {
      applicationInfo?.apply {
        sourceDir = archiveFilePath
        publicSourceDir = archiveFilePath
      }
    }
    successful = true
  } finally {
    if (!successful) {
      closeable?.close()
    }
  }
  return packageInfo to closeable
}


fun PackageManager.getPackageArchiveInfoCompat(archiveFilePath: String, flags: Int): PackageInfo? {
  var packageInfo = getPackageArchiveInfo(archiveFilePath, flags)
  // getPackageArchiveInfo() returns null for unsigned APKs if signing info is requested.
  if (packageInfo == null) {
    val flagsWithoutGetSigningInfo = flags.andInv(
      @Suppress("DEPRECATION")
      PackageManager.GET_SIGNATURES or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        PackageManager.GET_SIGNING_CERTIFICATES
      } else {
        0
      }
    )
    if (flags != flagsWithoutGetSigningInfo) {
      packageInfo = getPackageArchiveInfo(archiveFilePath, flagsWithoutGetSigningInfo)
        ?.apply {
          @Suppress("DEPRECATION")
          if (flags.hasBits(PackageManager.GET_SIGNATURES)) {
            signatures = emptyArray()
          }
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
            && flags.hasBits(PackageManager.GET_SIGNING_CERTIFICATES)
          ) {
            signingInfo = SigningInfo()
          }
        }
    }
  }
  return packageInfo
}
