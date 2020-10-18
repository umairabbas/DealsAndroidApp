package com.regionaldeals.de

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.KeyEvent
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController

class SubscribeNewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.subscribe_new_activity)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.navController)
        setupActionBarWithNavController(navController)
    }

    override fun onBackPressed() {
        val controller = findNavController(R.id.navController)
        val dest = controller.currentDestination
        dest?.label?.let {
            if(it.equals(getString(R.string.terms_cond))) {
                return
            }
        }
        super.onBackPressed()
    }

    override fun onSupportNavigateUp():Boolean {
        val controller = findNavController(R.id.navController)
        val dest = controller.currentDestination
        dest?.label?.let {
            if(it.equals(getString(R.string.terms_cond))) {
                return false
            }
        }
        return controller.navigateUp()
    }
}