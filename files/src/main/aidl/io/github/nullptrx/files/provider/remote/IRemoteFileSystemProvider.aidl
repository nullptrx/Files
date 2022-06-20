package io.github.nullptrx.files.provider.remote;

import io.github.nullptrx.files.provider.remote.ParcelableCopyOptions;
import io.github.nullptrx.files.provider.remote.ParcelableDirectoryStream;
import io.github.nullptrx.files.provider.remote.ParcelableException;
import io.github.nullptrx.files.provider.remote.ParcelableFileAttributes;
import io.github.nullptrx.files.provider.remote.ParcelableObject;
import io.github.nullptrx.files.provider.remote.ParcelablePathListConsumer;
import io.github.nullptrx.files.provider.remote.ParcelableSerializable;
import io.github.nullptrx.files.provider.remote.RemotePathObservable;
import io.github.nullptrx.files.provider.remote.RemoteInputStream;
import io.github.nullptrx.files.provider.remote.RemoteSeekableByteChannel;
import io.github.nullptrx.files.provider.remote.RemoteCallback;

interface IRemoteFileSystemProvider {
    RemoteInputStream newInputStream(
        in ParcelableObject file,
        in ParcelableSerializable options,
        out ParcelableException exception
    );

    RemoteSeekableByteChannel newByteChannel(
        in ParcelableObject file,
        in ParcelableSerializable options,
        in ParcelableFileAttributes attributes,
        out ParcelableException exception
    );

    ParcelableDirectoryStream newDirectoryStream(
        in ParcelableObject directory,
        in ParcelableObject filter,
        out ParcelableException exception
    );

    void createDirectory(
        in ParcelableObject directory,
        in ParcelableFileAttributes attributes,
        out ParcelableException exception
    );

    void createSymbolicLink(
        in ParcelableObject link,
        in ParcelableObject target,
        in ParcelableFileAttributes attributes,
        out ParcelableException exception
    );

    void createLink(
        in ParcelableObject link,
        in ParcelableObject existing,
        out ParcelableException exception
    );

    void delete(in ParcelableObject path, out ParcelableException exception);

    ParcelableObject readSymbolicLink(in ParcelableObject link, out ParcelableException exception);

    RemoteCallback copy(
        in ParcelableObject source,
        in ParcelableObject target,
        in ParcelableCopyOptions options,
        in RemoteCallback callback
    );

    RemoteCallback move(
        in ParcelableObject source,
        in ParcelableObject target,
        in ParcelableCopyOptions options,
        in RemoteCallback callback
    );

    boolean isSameFile(
        in ParcelableObject path,
        in ParcelableObject path2,
        out ParcelableException exception
    );

    boolean isHidden(in ParcelableObject path, out ParcelableException exception);

    ParcelableObject getFileStore(in ParcelableObject path, out ParcelableException exception);

    void checkAccess(
        in ParcelableObject path,
        in ParcelableSerializable modes,
        out ParcelableException exception
    );

    ParcelableObject readAttributes(
        in ParcelableObject path,
        in ParcelableSerializable type,
        in ParcelableSerializable options,
        out ParcelableException exception
    );

    RemotePathObservable observe(
        in ParcelableObject path,
        long intervalMillis,
        out ParcelableException exception
    );

    RemoteCallback search(
        in ParcelableObject directory,
        in String query,
        long intervalMillis,
        in ParcelablePathListConsumer listener,
        in RemoteCallback callback
    );
}
