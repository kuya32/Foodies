package com.macode.foodies.view.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.macode.foodies.databinding.SingleItemListBinding
import com.macode.foodies.view.activities.AddUpdateDishActivity

class CustomListItemAdapter(private val activity: Activity, private val listItems: List<String>, private val selection: String): RecyclerView.Adapter<CustomListItemAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: SingleItemListBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SingleItemListBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItems[position]
        with(holder) {
            binding.singleListItem.text = item
            itemView.setOnClickListener {
                if (activity is AddUpdateDishActivity) {
                    activity.selectedListItem(item, selection)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }
}
