package com.inoo.sutoriapp.ui.story.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.inoo.sutoriapp.R
import com.inoo.sutoriapp.data.pref.SessionViewModel
import com.inoo.sutoriapp.data.pref.SessionViewModelFactory
import com.inoo.sutoriapp.data.pref.SutoriAppPreferences
import com.inoo.sutoriapp.data.pref.dataStore
import com.inoo.sutoriapp.databinding.ActivityStoryBinding
import com.inoo.sutoriapp.ui.MainActivity
import com.inoo.sutoriapp.ui.story.ui.addstory.AddStoryActivity
import com.inoo.sutoriapp.utils.Utils.showToast

class StoryActivity : AppCompatActivity() {
    private var _binding: ActivityStoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var toolbar: Toolbar
    private lateinit var fabAddStory: FloatingActionButton

    private lateinit var pref: SutoriAppPreferences

    private val sessionViewModel: SessionViewModel by viewModels {
        SessionViewModelFactory.getInstance(this, pref)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = SutoriAppPreferences.getInstance(dataStore)

        toolbar = binding.toolbar
        fabAddStory = binding.fabAddStory
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            R.id.action_settings -> {
                val navController = findNavController(R.id.nav_host_fragment)
                if (navController.currentDestination?.id != R.id.settingsFragment) {
                    navController.navigate(R.id.action_listStoryFragment_to_settingsFragment)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun logout() {
        val title = getString(R.string.logout)
        val message = getString(R.string.logout_message)
        val builder = AlertDialog.Builder(this@StoryActivity)

        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                performLogout()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun performLogout() {
        showToast(this, getString(R.string.logout_process))
        sessionViewModel.logout()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
