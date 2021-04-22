package com.macode.foodies.view.fragments

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.macode.foodies.R
import com.macode.foodies.application.FavDishApplication
import com.macode.foodies.databinding.FragmentDishDetailsBinding
import com.macode.foodies.viewmodel.FavDishViewModel
import com.macode.foodies.viewmodel.FavDishViewModelFactory
import java.io.IOException

class DishDetailsFragment : Fragment() {

    private var binding: FragmentDishDetailsBinding? = null

    private val favDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDishDetailsBinding.inflate(inflater, container, false)
        val view = binding!!.root

        setUpToolbar(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: DishDetailsFragmentArgs by navArgs()
        args.let {
            try {
                binding?.let { it1 ->
                    Glide
                        .with(requireActivity())
                        .load(it.dishDetails.image)
                        .listener(object: RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.e("DishDetails", "Error loading image", e)
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                resource.let {
                                    Palette.from(resource!!.toBitmap()).generate() { palette ->
                                        val intColor = palette?.vibrantSwatch?.rgb ?: 0
                                        binding!!.dishDetailMainFrame.setBackgroundColor(intColor)
                                    }
                                }
                                return false
                            }

                        })
                        .centerCrop()
                        .into(it1.dishDetailImage)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            binding!!.dishDetailTitle.text = it.dishDetails.title
            binding!!.dishDetailType.text = it.dishDetails.type
            binding!!.dishDetailCategory.text = it.dishDetails.category
            binding!!.dishDetailIngredient.text = it.dishDetails.ingredients
            binding!!.dishDetailDirection.text = it.dishDetails.directionToCook
            binding!!.dishDetailCookingTime.text = resources.getString(R.string.estimateCookingTime, it.dishDetails.cookingTIme)

            if(args.dishDetails.favoriteDish) {
                binding!!.favoriteButton.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.favorite_selected))
            } else {
                binding!!.favoriteButton.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.favorite_unselected))
            }
        }

        binding!!.favoriteButton.setOnClickListener {
            args.dishDetails.favoriteDish = !args.dishDetails.favoriteDish

            favDishViewModel.update(args.dishDetails)

            if(args.dishDetails.favoriteDish) {
                binding!!.favoriteButton.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.favorite_selected))
                Toast.makeText(requireActivity(), "Dish added to favorites!", Toast.LENGTH_SHORT).show()
            } else {
                binding!!.favoriteButton.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.favorite_unselected))
                Toast.makeText(requireActivity(), "Dish removed from favorites!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setUpToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.dishDetailToolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Dish Details"
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
    }

}