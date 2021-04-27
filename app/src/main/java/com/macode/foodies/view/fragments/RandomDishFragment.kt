package com.macode.foodies.view.fragments

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.macode.foodies.R
import com.macode.foodies.application.FavDishApplication
import com.macode.foodies.databinding.FragmentRandomDishBinding
import com.macode.foodies.model.entities.FavDish
import com.macode.foodies.model.entities.RandomDish
import com.macode.foodies.utilities.Constants
import com.macode.foodies.viewmodel.FavDishViewModel
import com.macode.foodies.viewmodel.FavDishViewModelFactory
import com.macode.foodies.viewmodel.RandomDishViewModel

class RandomDishFragment : Fragment() {

    private var binding: FragmentRandomDishBinding? = null
    private lateinit var randomDishViewModel: RandomDishViewModel
    private var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRandomDishBinding.inflate(inflater, container, false)
        val view = binding!!.root

        setUpToolbar(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        randomDishViewModel = ViewModelProvider(this@RandomDishFragment).get(RandomDishViewModel::class.java)

        randomDishViewModel.getRandomRecipeFromAPI()

        randomDishViewModelObserver()

        binding!!.swipeRefreshRandomDishLayout.setOnRefreshListener {
            randomDishViewModel.getRandomRecipeFromAPI()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun randomDishViewModelObserver() {
        randomDishViewModel.randomDishResponse.observe(viewLifecycleOwner, {randomDishResponse ->
            randomDishResponse?.let {
            if(binding!!.swipeRefreshRandomDishLayout.isRefreshing) {
                binding!!.swipeRefreshRandomDishLayout.isRefreshing = false
            }
            setRandomDishResponseInUI(randomDishResponse.recipes[0])
        }})
        randomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner, {dataError ->
            dataError?.let {
            Log.e("Random Dish API Error", "$dataError")
            if(binding!!.swipeRefreshRandomDishLayout.isRefreshing) {
                binding!!.swipeRefreshRandomDishLayout.isRefreshing = false
            }
        }})

        randomDishViewModel.loadRandomDish.observe(viewLifecycleOwner, {loadRandomDish ->
            loadRandomDish?.let {
                Log.e("Random Dish Loading", "$loadRandomDish")
                if (loadRandomDish && !binding!!.swipeRefreshRandomDishLayout.isRefreshing) {
                    showCustomProgressDialog()
                } else {
                    hideCustomProgressDialog()
                }
        }})
    }

    private fun setRandomDishResponseInUI(recipe: RandomDish.Recipe) {
        Glide
            .with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(binding!!.randomDishImage)

        binding!!.randomDishTitle.text = recipe.title

        var dishType: String = "Other"
        if (recipe.dishTypes.isNotEmpty()) {
            dishType = recipe.dishTypes[0]
            binding!!.randomDishType.text = dishType
        }

        binding!!.randomDishCategory.text = "Other"

        var ingredients = ""
        for (value in recipe.extendedIngredients) {
            if (ingredients.isEmpty()) {
                ingredients = value.original
            } else {
                ingredients = ingredients + ", \n" + value.original
            }
        }
        binding!!.randomDishIngredient.text = ingredients

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding!!.randomDishDirection.text = Html.fromHtml(
                recipe.instructions,
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            binding!!.randomDishDirection.text = Html.fromHtml(recipe.instructions)
        }

        binding!!.favoriteButton.setImageDrawable(
            ContextCompat.getDrawable(requireActivity(), R.drawable.favorite_unselected)
        )

        var addedToFavorite = false

        binding!!.randomDishCookingTime.text = resources.getString(R.string.estimateCookingTime, recipe.readyInMinutes.toString())

        // TODO: Repeated random dishes if originally added to favorites are being duplicated in the all dishes recycler view
        binding!!.favoriteButton.setOnClickListener {

            if (addedToFavorite) {
                Toast.makeText(requireActivity(), resources.getString(R.string.alreadyAddedToFavorite), Toast.LENGTH_SHORT).show()
            } else {
                val randomDishDetails = FavDish(
                    recipe.image,
                    Constants.DISH_IMAGE_SOURCE_ONLINE,
                    recipe.title,
                    dishType,
                    "Other",
                    ingredients,
                    recipe.readyInMinutes.toString(),
                    recipe.instructions,
                    true
                )

                val favDishViewModel: FavDishViewModel by viewModels {
                    FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
                }
                favDishViewModel.insert(randomDishDetails)

                addedToFavorite = true

                binding!!.favoriteButton.setImageDrawable(
                    ContextCompat.getDrawable(requireActivity(), R.drawable.favorite_selected)
                )
                Toast.makeText(requireActivity(), "Dish added to favorites!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpToolbar(view: View) {
        val toolbar =  view.findViewById<Toolbar>(R.id.randomDishToolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Random Dish"
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
    }

    private fun showCustomProgressDialog() {
        progressDialog = Dialog(requireActivity())
        progressDialog?.let {
            it.setContentView(R.layout.custom_progress_dialog)
            it.show()
        }
    }

    private fun hideCustomProgressDialog() {
        progressDialog?.dismiss()
    }
}