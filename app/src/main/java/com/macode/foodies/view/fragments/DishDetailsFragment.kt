package com.macode.foodies.view.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.macode.foodies.R
import com.macode.foodies.databinding.FragmentDishDetailsBinding
import java.io.IOException

class DishDetailsFragment : Fragment() {

    private var binding: FragmentDishDetailsBinding? = null

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