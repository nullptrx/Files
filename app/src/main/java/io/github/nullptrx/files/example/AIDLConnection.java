package io.github.nullptrx.files.example;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

class AIDLConnection implements ServiceConnection {
  private static final String TAG = "AIDLConnection";

  AIDLConnection() {

  }

  @Override
  public void onServiceConnected(ComponentName name, IBinder service) {
    Log.d(TAG, "AIDL onServiceConnected");

    ITestService ipc = ITestService.Stub.asInterface(service);
    try {
      Log.e(TAG, "Remote success");
    } catch (Exception e) {
      Log.e(TAG, "Remote error", e);
    }
  }

  @Override
  public void onServiceDisconnected(ComponentName name) {
    Log.d(TAG, "AIDL onServiceDisconnected");
  }
}