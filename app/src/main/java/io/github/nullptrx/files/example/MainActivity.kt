package io.github.nullptrx.files.example

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import io.github.nullptrx.files.example.databinding.ActivityMainBinding
import io.github.nullptrx.files.provider.common.path.newDirectoryStream
import java8.nio.file.Paths
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

  private lateinit var appBarConfiguration: AppBarConfiguration
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setSupportActionBar(binding.toolbar)

    val navController = findNavController(R.id.nav_host_fragment_content_main)
    appBarConfiguration = AppBarConfiguration(navController.graph)
    setupActionBarWithNavController(navController, appBarConfiguration)

    // binding.fab.setOnClickListener { view ->
    //
    //   launch(Dispatchers.IO) {
    //     try {
    //       val path = Paths.get(Environment.getRootDirectory().absolutePath)
    //       path.newDirectoryStream().forEach {
    //         println(it.toString())
    //       }
    //     } catch (e: Exception) {
    //       e.printStackTrace()
    //     }
    //   }
    // }
    // binding.fab.setOnClickListener { view ->
    //
    //   try {
    //     LibSuFileServiceLauncher.launchService()
    //   } catch (e: Exception) {
    //     e.printStackTrace()
    //   }
    // }
    binding.fab.setOnClickListener { view ->
      // val intent = Intent(this, LibSuFileService::class.java)
      // RootService.bind(intent, LibSuConnection())

      try {
        Paths.get("/").newDirectoryStream().forEach {
          System.err.println(it.toString())
        }
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    return when (item.itemId) {
      R.id.action_settings -> true
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onSupportNavigateUp(): Boolean {
    val navController = findNavController(R.id.nav_host_fragment_content_main)
    return navController.navigateUp(appBarConfiguration)
        || super.onSupportNavigateUp()
  }
}