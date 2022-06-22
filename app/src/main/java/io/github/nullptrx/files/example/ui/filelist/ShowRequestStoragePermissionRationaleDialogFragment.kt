package io.github.nullptrx.files.example.ui.filelist

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.nullptrx.files.example.R
import io.github.nullptrx.files.example.extension.show

class ShowRequestStoragePermissionRationaleDialogFragment : AppCompatDialogFragment() {
  private val listener: Listener
    get() = requireParentFragment() as Listener

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return MaterialAlertDialogBuilder(requireContext(), theme)
      .setMessage(R.string.storage_permission_rationale_message)
      .setPositiveButton(android.R.string.ok) { _, _ -> listener.requestStoragePermission() }
      .setNegativeButton(android.R.string.cancel, null)
      .create()
  }

  companion object {
    fun show(fragment: Fragment) {
      ShowRequestStoragePermissionRationaleDialogFragment().show(fragment)
    }
  }

  interface Listener {
    fun requestStoragePermission()
  }
}