package io.github.nullptrx.files.example.ui.filelist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import io.github.nullptrx.files.example.extension.extraPath
import io.github.nullptrx.files.extension.createIntent
import io.github.nullptrx.files.file.MimeType
import io.github.nullptrx.files.provider.common.path.option.putArgs
import java8.nio.file.Path

class FileListActivity : AppCompatActivity() {

  private lateinit var fragment: FileListFragment

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Calls ensureSubDecor().
    findViewById<View>(android.R.id.content)
    if (savedInstanceState == null) {
      fragment = FileListFragment().putArgs(FileListFragment.Args(intent))
      supportFragmentManager.commit { add(android.R.id.content, fragment) }
    } else {
      fragment = supportFragmentManager.findFragmentById(android.R.id.content)
          as FileListFragment
    }
  }

  override fun onBackPressed() {
    if (fragment.onBackPressed()) {
      return
    }
    super.onBackPressed()
  }

  companion object {
    fun createViewIntent(path: Path): Intent =
      FileListActivity::class.createIntent()
        .setAction(Intent.ACTION_VIEW)
        .apply { extraPath = path }
  }

  class PickDirectoryContract : ActivityResultContract<Path?, Path?>() {
    override fun createIntent(context: Context, input: Path?): Intent =
      FileListActivity::class.createIntent()
        .setAction(Intent.ACTION_OPEN_DOCUMENT_TREE)
        .apply { input?.let { extraPath = it } }

    override fun parseResult(resultCode: Int, intent: Intent?): Path? =
      if (resultCode == RESULT_OK) intent?.extraPath else null
  }

  class PickFileContract : ActivityResultContract<List<MimeType>, Path?>() {
    override fun createIntent(context: Context, input: List<MimeType>): Intent =
      FileListActivity::class.createIntent()
        .setAction(Intent.ACTION_OPEN_DOCUMENT)
        .setType(MimeType.ANY.value)
        .putExtra(Intent.EXTRA_MIME_TYPES, input.map { it.value }.toTypedArray())

    override fun parseResult(resultCode: Int, intent: Intent?): Path? =
      if (resultCode == RESULT_OK) intent?.extraPath else null
  }
}