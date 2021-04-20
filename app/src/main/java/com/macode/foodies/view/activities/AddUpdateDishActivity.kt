package com.macode.foodies.view.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.BitmapCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.textfield.TextInputLayout
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.macode.foodies.R
import com.macode.foodies.application.FavDishApplication
import com.macode.foodies.databinding.ActivityAddUpdateDishBinding
import com.macode.foodies.databinding.CustomListDialogBinding
import com.macode.foodies.databinding.ImageSelectionDialogBinding
import com.macode.foodies.model.entities.FavDish
import com.macode.foodies.utilities.Constants
import com.macode.foodies.view.adapters.CustomListItemAdapter
import com.macode.foodies.viewmodel.FavDishViewModel
import com.macode.foodies.viewmodel.FavDishViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val CAMERA = 1
        const val GALLERY = 2
        const val IMAGE_DIRECTORY = "FavDishImages"
    }

    private lateinit var binding: ActivityAddUpdateDishBinding
    private lateinit var customListDialog: Dialog
    private var imagePath: String = ""

    private val favDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpToolbar()

        binding.addImageButton.setOnClickListener(this)
        binding.typeEditInput.setOnClickListener(this)
        binding.categoryEditInput.setOnClickListener(this)
        binding.cookingTimeMinutesEditInput.setOnClickListener(this)
        binding.addDishButton.setOnClickListener(this)
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

                    imagePath = saveImageToInternalStorage(data.extras!!.get("data") as Bitmap)
                }
            } else if (requestCode == GALLERY) {
                data?.let {
                    Glide
                        .with(this)
                        .load(data.data)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(object: RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.e("GallerySelection", "Error loading image!", e)
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                resource?.let {
                                    val bitmap: Bitmap = resource.toBitmap()
                                    imagePath = saveImageToInternalStorage(bitmap)
                                    Log.i("ImagePath", imagePath)
                                }
                                return false
                            }

                        })
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
                R.id.addDishButton -> {
                    println(binding.addDishImage.drawable.toString())
                    addDish()
                }
            }
        }
    }

    private fun addDish() {
        val title = binding.titleEditInput.text.toString()
        val type = binding.typeEditInput.text.toString()
        val category = binding.categoryEditInput.text.toString()
        val ingredients = binding.ingredientsEditInput.text.toString()
        val cookingTime = binding.cookingTimeMinutesEditInput.text.toString()
        val directions = binding.directionsEditInput.text.toString()

        when {
            imagePath.isEmpty() -> {
                Toast.makeText(this@AddUpdateDishActivity, "Please include an image to dish!", Toast.LENGTH_SHORT).show()
            }
            title.isEmpty() -> {
                showError(binding.titleInput, "Please enter a title for the dish!")
            }
            type.isEmpty() -> {
                showError(binding.typeInput, "Please choose a type for the dish!")
            }
            category.isEmpty() -> {
                showError(binding.categoryInput, "Please choose a category for the dish!")
            }
            ingredients.isEmpty() -> {
                showError(binding.ingredientsInput, "Please enter the ingredients for the dish!")
            }
            cookingTime.isEmpty() -> {
                showError(binding.cookingTimeMinutesInput, "Please choose a cooking time for the dish!")
            }
            directions.isEmpty() -> {
                showError(binding.directionsInput, "Please enter directions for the dish!")
            }
            else -> {
                val favDishDetails: FavDish = FavDish(
                    imagePath,
                    Constants.DISH_IMAGE_SOURCE_LOCAL,
                    title,
                    type,
                    category,
                    ingredients,
                    cookingTime,
                    directions,
                    false
                )

                favDishViewModel.insert(favDishDetails)
                Toast.makeText(this@AddUpdateDishActivity, "Dish added!", Toast.LENGTH_SHORT).show()
                Log.i("AddedDish", "Successfully added dish!")
                finish()
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
                    dialog.dismiss()
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

    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val wrapper = ContextWrapper(applicationContext)

        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.png")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file.absolutePath
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

    private fun showError(layout: TextInputLayout, message: String) {
        layout.error = message
        layout.requestFocus()
    }
}