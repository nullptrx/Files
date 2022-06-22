package io.github.nullptrx.files.example.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

abstract class AppActivity : AppCompatActivity() {
  private var isDelegateCreated = false

  override fun getDelegate(): AppCompatDelegate {
    val delegate = super.getDelegate()

    if (!isDelegateCreated) {
      isDelegateCreated = true
      // NightModeHelper.apply(this)
    }
    return delegate
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    // CustomThemeHelper.apply(this)

    super.onCreate(savedInstanceState)
  }

  override fun onSupportNavigateUp(): Boolean {
    if (!super.onSupportNavigateUp()) {
      finish()
    }
    return true
  }
}
