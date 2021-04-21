package com.macode.foodies.view.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.macode.foodies.R
import com.macode.foodies.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        navController = findNavController(R.id.navHostFragment)
        val bottomNavigationConfiguration = AppBarConfiguration(setOf(
            R.id.navigationAllDishes, R.id.navigationFavoriteDishes, R.id.navigationRandomDish
        ))
        setupActionBarWithNavController(navController, bottomNavigationConfiguration)
        binding.bottomNavView.setupWithNavController(navController)
    }

    private fun setUpActionBar() {
        val toolbar = findViewById<Toolbar>(R.id.mainToolbar)
        setSupportActionBar(toolbar)
        toolbar.title = "Foodies"
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFFFF"))
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    fun hideBottomNavigationView() {
        binding.bottomNavView.clearAnimation()
        binding.bottomNavView.animate().translationY(binding.bottomNavView.height.toFloat()).duration = 300
        Handler(Looper.getMainLooper()).postDelayed({
            binding.bottomNavView.visibility = View.GONE
        }, 280)
    }

    fun showBottomNavigationView() {
        binding.bottomNavView.clearAnimation()
        binding.bottomNavView.animate().translationY(0f).duration = 300
        binding.bottomNavView.visibility = View.VISIBLE
    }
}