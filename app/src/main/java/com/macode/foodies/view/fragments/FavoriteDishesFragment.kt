package com.macode.foodies.view.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.macode.foodies.R
import com.macode.foodies.application.FavDishApplication
import com.macode.foodies.databinding.FragmentFavoriteDishesBinding
import com.macode.foodies.model.entities.FavDish
import com.macode.foodies.view.activities.MainActivity
import com.macode.foodies.view.adapters.FavDishAdapter
import com.macode.foodies.viewmodel.DashboardViewModel
import com.macode.foodies.viewmodel.FavDishViewModel
import com.macode.foodies.viewmodel.FavDishViewModelFactory

class FavoriteDishesFragment : Fragment() {

    private var binding: FragmentFavoriteDishesBinding? = null


    private val favDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)
        val view = binding!!.root

        setUpToolbar(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.favoriteDishesRecyclerView.layoutManager = GridLayoutManager(requireActivity(), 2)
        val adapter = FavDishAdapter(this@FavoriteDishesFragment)
        binding!!.favoriteDishesRecyclerView.adapter = adapter

        favDishViewModel.favoriteDishList.observe(viewLifecycleOwner) { dishes ->
            dishes.let {
                if(it.isNotEmpty()) {
                    binding!!.favoriteDishesRecyclerView.visibility = View.VISIBLE
                    binding!!.noFavoriteDishesAddedText.visibility = View.GONE

                    adapter.dishesList(it)
                } else {
                    binding!!.favoriteDishesRecyclerView.visibility = View.GONE
                    binding!!.noFavoriteDishesAddedText.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    fun dishDetails(favDish: FavDish) {
        findNavController().navigate(FavoriteDishesFragmentDirections.actionNavigationFavoriteDishesToNavigationDishDetails(favDish))

        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }
    }

    private fun setUpToolbar(view: View) {
        val toolbar =  view.findViewById<Toolbar>(R.id.favoriteDishesToolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Favorite Dishes"
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
    }
}