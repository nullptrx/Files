// IRemoteCallback.aidl
package io.github.nullptrx.files.provider.remote;

import android.os.Bundle;

interface IRemoteCallback {
    void sendResult(in Bundle result);
}