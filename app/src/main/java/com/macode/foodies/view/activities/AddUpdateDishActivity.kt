package com.macode.foodies.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.macode.foodies.R
import com.macode.foodies.databinding.ActivityAddUpdateDishBinding
import com.macode.foodies.databinding.CustomListDialogBinding
import com.macode.foodies.databinding.ImageSelectionDialogBinding
import com.macode.foodies.utilities.Constants
import com.macode.foodies.view.adapters.CustomListItemAdapter
import java.io.IOException

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val CAMERA = 1
        const val GALLERY = 2
    }

    private lateinit var binding: ActivityAddUpdateDishBinding
    private lateinit var customListDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar()

        binding.addImageButton.setOnClickListener(this)
        binding.typeEditInput.setOnClickListener(this)
        binding.categoryEditInput.setOnClickListener(this)
        binding.cookingTimeMinutesEditInput.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA) {
                data?.extras?.let {
                    Glide
                        .with(this)
                        .load(data.extras!!.get("data"))
                        .centerCrop()
                        .placeholder(R.drawable.gallery)
                        .into(binding.addDishImage)
                }
            } else if (requestCode == GALLERY) {
                data?.let {
                    Glide
                        .with(this)
                        .load(data.data)
                        .centerCrop()
                        .placeholder(R.drawable.add_image)
                        .into(binding.addDishImage)
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Cancelled", "User cancelled image selection")
        }
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
                    return
                }
                R.id.typeEditInput -> {
                    customItemsDialog("Select Dish Type", Constants.dishTypes(), Constants.DISH_TYPES)
                    return
                }
                R.id.categoryEditInput -> {
                    customItemsDialog("Select Dish Category", Constants.dishCategories(), Constants.DISH_CATEGORY)
                    return
                }
                R.id.cookingTimeMinutesEditInput -> {
                    customItemsDialog("Select Cooking Time in Minutes", Constants.dishCookingTimes(), Constants.DISH_COOKING_TIME)
                    return
                }
            }
        }
    }

    private fun imageSelectionDialog() {
        val dialog = Dialog(this)
        val dialogBinding: ImageSelectionDialogBinding = ImageSelectionDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.cameraSelection.setOnClickListener {
            Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(intent, CAMERA)
                            dialog.dismiss()
                        } else {
                            showRationalDialogForPermissions()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }

            }).onSameThread().check()
        }

        dialogBinding.gallerySelection.setOnClickListener {
            Dexter.withContext(this).withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object: PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, GALLERY)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(this@AddUpdateDishActivity, "You have denied the storage permission to select image from gallery!", Toast.LENGTH_SHORT).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }


            }).onSameThread().check()
        }

        dialog.show()
    }

    private fun customItemsDialog(title: String, itemsList: List<String>, selection: String) {
        customListDialog = Dialog(this)
        val binding: CustomListDialogBinding = CustomListDialogBinding.inflate(layoutInflater)
        customListDialog.setContentView(binding.root)

        binding.dialogListTitle.text = title
        binding.dialogListRecyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = CustomListItemAdapter(this, itemsList, selection)
        binding.dialogListRecyclerView.adapter = adapter

        customListDialog.show()
    }

    fun selectedListItem(item: String, selection: String) {
        when (selection) {
            Constants.DISH_TYPES -> {
                customListDialog.dismiss()
                binding.typeEditInput.setText(item)
            }
            Constants.DISH_CATEGORY -> {
                customListDialog.dismiss()
                binding.categoryEditInput.setText(item)
            }
            Constants.DISH_COOKING_TIME -> {
                customListDialog.dismiss()
                binding.cookingTimeMinutesEditInput.setText(item)
            }
        }
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this).setMessage("It looks like you have turned off permission required for this feature. " +
                "It can be enabled under Application Settings.")
            .setPositiveButton("Go to settings")
            {_, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel")
            {dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}