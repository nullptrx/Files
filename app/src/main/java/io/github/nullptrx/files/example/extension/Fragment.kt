package io.github.nullptrx.files.example.extension

import android.widget.Toast
import androidx.annotation.PluralsRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

fun Fragment.showToast(textRes: Int, duration: Int = Toast.LENGTH_SHORT) =
  requireContext().showToast(textRes, duration)

fun Fragment.showToast(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) =
  requireContext().showToast(text, duration)

fun Fragment.checkSelfPermission(permission: String) =
  requireContext().checkSelfPermissionCompat(permission)

fun Fragment.finish() = requireActivity().finish()

fun Fragment.getQuantityString(@PluralsRes id: Int, quantity: Int): String =
  requireContext().getQuantityString(id, quantity)

fun Fragment.getQuantityString(
  @PluralsRes id: Int,
  quantity: Int,
  vararg formatArgs: Any?
): String = requireContext().getQuantityString(id, quantity, *formatArgs)

inline fun <reified VM : ViewModel> Fragment.viewModels(
  noinline ownerProducer: () -> ViewModelStoreOwner = { this },
  noinline factoryProducer: (() -> () -> VM)? = null
) = viewModels<VM>(
  ownerProducer,
  factoryProducer?.let {
    {
      val factory = it()
      object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>) = factory() as T
      }
    }
  }
)

inline fun <reified VM : ViewModel> Fragment.activityViewModels(
  noinline factoryProducer: (() -> () -> VM)? = null
) = viewModels(::requireActivity, factoryProducer)
