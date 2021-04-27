package com.macode.foodies.view.adapters

import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.macode.foodies.R
import com.macode.foodies.databinding.SingleDishItemBinding
import com.macode.foodies.model.entities.FavDish
import com.macode.foodies.utilities.Constants
import com.macode.foodies.view.activities.AddUpdateDishActivity
import com.macode.foodies.view.fragments.AllDishesFragment
import com.macode.foodies.view.fragments.FavoriteDishesFragment

class FavDishAdapter(private val fragment: Fragment): RecyclerView.Adapter<FavDishAdapter.ViewHolder>() {

    private var dishes: List<FavDish> = listOf()

    inner class ViewHolder(val binding: SingleDishItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleDishItemBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    // TODO: Figure out why the text view is changing in item view when editing other items text view
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]
        with(holder) {
            Glide
                .with(fragment)
                .load(dish.image)
                .into(binding.singleDishImage)
            binding.singleDishTitle.text = dish.title

            itemView.setOnClickListener {
                if (fragment is AllDishesFragment) {
                    fragment.dishDetails(dish)
                } else if (fragment is FavoriteDishesFragment) {
                    fragment.dishDetails(dish)
                }
            }

            if (fragment is AllDishesFragment) {
                binding.singleDishMoreOptions.visibility = View.VISIBLE
            } else if (fragment is FavoriteDishesFragment) {
                binding.singleDishMoreOptions.visibility = View.GONE
            }

            binding.singleDishMoreOptions.setOnClickListener {
                val popUp = PopupMenu(fragment.context, binding.singleDishMoreOptions)
                popUp.menuInflater.inflate(R.menu.single_dish_item_menu, popUp.menu)

                popUp.setOnMenuItemClickListener {
                    if (it.itemId == R.id.editDish) {
                        val intent = Intent(fragment.requireActivity(), AddUpdateDishActivity::class.java)
                        intent.putExtra(Constants.EXTRA_DISH_DETAILS, dish)
                        fragment.requireActivity().startActivity(intent)
                    } else if (it.itemId == R.id.deleteDish) {
                        if (fragment is AllDishesFragment) {
                            fragment.deleteDish(dish)
                        }
                    }
                    true
                }

                popUp.show()
            }
        }
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    fun dishesList(list: List<FavDish>) {
        dishes = list
        notifyDataSetChanged()
    }
}