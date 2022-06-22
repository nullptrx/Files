package io.github.nullptrx.files.example.storage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.commit
import io.github.nullptrx.files.example.ui.AppActivity
import io.github.nullptrx.files.provider.common.path.option.args
import io.github.nullptrx.files.provider.common.path.option.putArgs

class EditDeviceStorageDialogActivity : AppActivity() {
  private val args by args<EditDeviceStorageDialogFragment.Args>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Calls ensureSubDecor().
    findViewById<View>(android.R.id.content)
    if (savedInstanceState == null) {
      val fragment = EditDeviceStorageDialogFragment().putArgs(args)
      supportFragmentManager.commit {
        add(fragment, EditDeviceStorageDialogFragment::class.java.name)
      }
    }
  }
}
