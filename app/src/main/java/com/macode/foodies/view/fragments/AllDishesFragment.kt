package com.macode.foodies.view.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.macode.foodies.R
import com.macode.foodies.application.FavDishApplication
import com.macode.foodies.databinding.CustomListDialogBinding
import com.macode.foodies.databinding.FragmentAllDishesBinding
import com.macode.foodies.databinding.SingleItemListBinding
import com.macode.foodies.model.entities.FavDish
import com.macode.foodies.utilities.Constants
import com.macode.foodies.view.activities.AddUpdateDishActivity
import com.macode.foodies.view.activities.MainActivity
import com.macode.foodies.view.adapters.CustomListItemAdapter
import com.macode.foodies.view.adapters.FavDishAdapter
import com.macode.foodies.viewmodel.FavDishViewModel
import com.macode.foodies.viewmodel.FavDishViewModelFactory
import com.macode.foodies.viewmodel.HomeViewModel

class AllDishesFragment : Fragment() {

    private var binding: FragmentAllDishesBinding? = null
    private lateinit var favDishAdapter: FavDishAdapter
    private lateinit var customListDialog: Dialog

    private val favDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentAllDishesBinding.inflate(inflater, container, false)
        val view = binding!!.root

        setUpToolbar(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.dishesListRecyclerView.layoutManager = GridLayoutManager(requireActivity(), 2)
        favDishAdapter = FavDishAdapter(this@AllDishesFragment)
        binding!!.dishesListRecyclerView.adapter = favDishAdapter

        favDishViewModel.allDishesList.observe(viewLifecycleOwner) {
            dishes ->
                dishes.let {
                    if (it.isNotEmpty()) {
                        binding!!.dishesListRecyclerView.visibility = View.VISIBLE
                        binding!!.noDishesAddedText.visibility = View.GONE

                        favDishAdapter.dishesList(it)
                    } else {
                        binding!!.dishesListRecyclerView.visibility = View.GONE
                        binding!!.noDishesAddedText.visibility = View.VISIBLE
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
        findNavController().navigate(AllDishesFragmentDirections.actionNavigationAllDishesToNavigationDishDetails(favDish))

        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("InflateParams")
    fun deleteDish(dish: FavDish) {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater: LayoutInflater = LayoutInflater.from(requireContext())
        val view: View = inflater.inflate(R.layout.alert_dialog_title, null)
        builder.setCustomTitle(view)
        builder.setMessage("Are you sure you want to delete ${dish.title}")
        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            dialogInterface.dismiss()
            favDishViewModel.delete(dish)
        }
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
        val messageView: TextView = alertDialog.requireViewById(android.R.id.message)
        messageView.gravity = Gravity.CENTER
        val positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
        val negativeButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
        positiveButton.setTextColor(Color.BLACK)
        negativeButton.setTextColor(Color.BLACK)
        val positiveButtonLinearLayout: LinearLayout.LayoutParams = positiveButton.layoutParams as LinearLayout.LayoutParams
        positiveButtonLinearLayout.weight = 10.0f
        positiveButton.layoutParams = positiveButtonLinearLayout
        negativeButton.layoutParams = positiveButtonLinearLayout
    }

    private fun sortDishesListDialog() {
        customListDialog = Dialog(requireActivity())
        val binding: CustomListDialogBinding = CustomListDialogBinding.inflate(layoutInflater)

        customListDialog.setContentView(binding.root)
        binding.dialogListTitle.text = resources.getString(R.string.selectItemToSortTitle)

        val dishTypes = Constants.dishTypes()
        dishTypes.add(0, Constants.ALL_ITEMS)
        binding.dialogListRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        val adapter = CustomListItemAdapter(requireActivity(), this@AllDishesFragment, dishTypes, Constants.FILTER_SELECTION)
        binding.dialogListRecyclerView.adapter = adapter
        customListDialog.show()
    }

    fun sortSelection(sortItemSelection: String) {
        customListDialog.dismiss()

        Log.i("Sort Selection", sortItemSelection)

        if (sortItemSelection == Constants.ALL_ITEMS) {
            favDishViewModel.allDishesList.observe(viewLifecycleOwner) {
                    dishes ->
                dishes.let {
                    if (it.isNotEmpty()) {
                        binding!!.dishesListRecyclerView.visibility = View.VISIBLE
                        binding!!.noDishesAddedText.visibility = View.GONE

                        favDishAdapter.dishesList(it)
                    } else {
                        binding!!.dishesListRecyclerView.visibility = View.GONE
                        binding!!.noDishesAddedText.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            favDishViewModel.getSortedList(sortItemSelection).observe(viewLifecycleOwner) { dishes ->
                dishes.let {
                    if (it.isNotEmpty()) {
                        binding!!.dishesListRecyclerView.visibility = View.VISIBLE
                        binding!!.noDishesAddedText.visibility = View.GONE

                        favDishAdapter.dishesList(it)
                    } else {
                        binding!!.dishesListRecyclerView.visibility = View.GONE
                        binding!!.noDishesAddedText.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setUpToolbar(view: View) {
        val toolbar = view.findViewById<Toolbar>(R.id.allDishesToolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "All Dishes"
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.all_dishes_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addDishAction -> {
                startActivity(Intent(requireActivity(), AddUpdateDishActivity::class.java))
                return true
            }
            R.id.sortDishesAction -> {
                sortDishesListDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}