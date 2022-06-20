package io.github.nullptrx.files.provider.remote;

import io.github.nullptrx.files.provider.remote.ParcelableException;
import io.github.nullptrx.files.provider.remote.RemoteCallback;

interface IRemotePathObservable {
    void addObserver(in RemoteCallback observer);

    void close(out ParcelableException exception);
}
