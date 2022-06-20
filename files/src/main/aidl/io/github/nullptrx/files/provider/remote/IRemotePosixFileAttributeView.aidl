package io.github.nullptrx.files.provider.remote;

import io.github.nullptrx.files.provider.common.attrs.ParcelableFileTime;
import io.github.nullptrx.files.provider.common.attrs.ParcelablePosixFileMode;
import io.github.nullptrx.files.provider.common.posix.PosixGroup;
import io.github.nullptrx.files.provider.common.posix.PosixUser;
import io.github.nullptrx.files.provider.remote.ParcelableException;
import io.github.nullptrx.files.provider.remote.ParcelableObject;

interface IRemotePosixFileAttributeView {
    ParcelableObject readAttributes(out ParcelableException exception);

    void setTimes(
        in ParcelableFileTime lastModifiedTime,
        in ParcelableFileTime lastAccessTime,
        in ParcelableFileTime createTime,
        out ParcelableException exception
    );

    void setOwner(in PosixUser owner, out ParcelableException exception);

    void setGroup(in PosixGroup group, out ParcelableException exception);

    void setMode(in ParcelablePosixFileMode mode, out ParcelableException exception);

    void setSeLinuxContext(in ParcelableObject context, out ParcelableException exception);

    void restoreSeLinuxContext(out ParcelableException exception);
}
