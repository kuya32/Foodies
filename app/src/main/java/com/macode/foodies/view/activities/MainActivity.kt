package com.macode.foodies.view.activities

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.macode.foodies.R
import com.macode.foodies.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpActionBar()

        val navController = findNavController(R.id.navHostFragment)
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
}