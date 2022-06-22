package io.github.nullptrx.files.example.extension


import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

fun DialogFragment.show(fragment: Fragment) {
  show(fragment.childFragmentManager, null)
}
