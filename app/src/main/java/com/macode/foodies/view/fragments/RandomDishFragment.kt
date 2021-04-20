package com.macode.foodies.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.macode.foodies.databinding.FragmentRandomDishBinding
import com.macode.foodies.viewmodel.NotificationsViewModel

class RandomDishFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)
        val binding = FragmentRandomDishBinding.inflate(inflater, container, false)
        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
            binding.notificationsText.text = it
        })

        return binding.root
    }
}