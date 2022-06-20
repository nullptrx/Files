package io.github.nullptrx.files.provider.remote;

import io.github.nullptrx.files.provider.remote.ParcelableException;

interface IRemoteFileSystem {
    void close(out ParcelableException exception);
}
