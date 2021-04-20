package com.macode.foodies.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.macode.foodies.databinding.FragmentFavoriteDishesBinding
import com.macode.foodies.viewmodel.DashboardViewModel

class FavoriteDishesFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        val binding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)
        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.dashboardText.text = it
        })

        return binding.root
    }

}