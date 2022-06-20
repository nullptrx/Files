package io.github.nullptrx.files.provider.common

import java8.nio.file.FileSystemException

class FileStoreNotFoundException(file: String?) : FileSystemException(file)