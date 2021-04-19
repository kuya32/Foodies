package com.macode.foodies.view.activities

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.macode.foodies.R
import com.macode.foodies.databinding.ActivityAddUpdateDishBinding
import com.macode.foodies.databinding.ImageSelectionDialogBinding

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddUpdateDishBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar()
        binding.addImageButton.setOnClickListener(this)
    }

    private fun setUpToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.addDishToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Dish"
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.addImageButton -> {
                    imageSelectionDialog()
                }

            }
        }
    }

    private fun imageSelectionDialog() {
        val dialog = Dialog(this)
        val dialogBinding: ImageSelectionDialogBinding = ImageSelectionDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.cameraSelection.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.gallerySelection.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}