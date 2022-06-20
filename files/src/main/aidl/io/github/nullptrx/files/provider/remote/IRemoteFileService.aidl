package io.github.nullptrx.files.provider.remote;

import io.github.nullptrx.files.provider.remote.IRemoteFileSystem;
import io.github.nullptrx.files.provider.remote.IRemoteFileSystemProvider;
import io.github.nullptrx.files.provider.remote.IRemotePosixFileAttributeView;
import io.github.nullptrx.files.provider.remote.IRemotePosixFileStore;
import io.github.nullptrx.files.provider.remote.ParcelableObject;

interface IRemoteFileService {
    IRemoteFileSystemProvider getRemoteFileSystemProviderInterface(String scheme);

    IRemoteFileSystem getRemoteFileSystemInterface(in ParcelableObject fileSystem);

    IRemotePosixFileStore getRemotePosixFileStoreInterface(in ParcelableObject fileStore);

    IRemotePosixFileAttributeView getRemotePosixFileAttributeViewInterface(
        in ParcelableObject attributeView
    );

    void refreshArchiveFileSystem(in ParcelableObject fileSystem);
}
