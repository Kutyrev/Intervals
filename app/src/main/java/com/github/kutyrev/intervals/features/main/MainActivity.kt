package com.github.kutyrev.intervals.features.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.github.kutyrev.intervals.R
import com.github.kutyrev.intervals.features.settings.SettingsFragment
import com.github.kutyrev.intervals.datasource.database.ListEntity
import com.github.kutyrev.intervals.features.detail.DetailFragment
import androidx.activity.addCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<MainListFragment>(R.id.fragment_container_view)
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            if (supportFragmentManager.backStackEntryCount == 0) {
                onBackPressedDispatcher.onBackPressed()
            } else {
                supportFragmentManager.popBackStack()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_settings -> {
                supportFragmentManager.commit {
                    val fragment = SettingsFragment()
                    setReorderingAllowed(true)
                    addToBackStack("")
                    replace(R.id.fragment_container_view, fragment)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun goToDetailFragment(list: ListEntity){
        supportFragmentManager.commit {
            val fragment = DetailFragment(list)
            setReorderingAllowed(true)
            addToBackStack("")
            replace(R.id.fragment_container_view, fragment)
        }
    }
}