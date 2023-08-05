package com.empire.strivefurniture.ui.homePage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.empire.strivefurniture.R
import com.empire.strivefurniture.databinding.FragmentHomeBinding
import com.empire.strivefurniture.models.FurnitureItem
import com.empire.strivefurniture.ui.itemPages.AddItemFragment
import com.empire.strivefurniture.utils.MethodUtils.showDialog
import com.empire.strivefurniture.viewModel.AppViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeFragment : Fragment() {
    private val vm: AppViewModel by activityViewModels()
    private val adapter: ItemsAdapter = ItemsAdapter()
    private lateinit var binding: FragmentHomeBinding
    private val firebaseUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        init()
        return binding.root
    }
    private fun init() {
        binding.apply {
            Glide.with(requireContext()).load(vm.user.value?.profileImage)
                .fallback(R.drawable.baseline_account_circle_24).into(profileButton)
            homeRecyclerView.adapter = adapter
            profileButton.setOnClickListener {

                if (firebaseUser != null) {
                    findNavController().navigate(R.id.action_global_profileFragment)
                } else {
                    showDialog(
                        requireContext(),
                        "Do you want to become a seller?",
                        "Unlock your potential as a seller with our comprehensive platform designed to empower your entrepreneurial journey.",
                        findNavController()
                    )
                }

            }

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    searchItems(query)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchItems(newText)
                    return true
                }
            }
            )
            cartFab.setOnClickListener {
                findNavController().navigate(R.id.action_global_cartFragment)
            }

            vm.cartItems.observe(requireActivity()) {
                val itemSize = it.size
                cartFab.text = "$itemSize items in Cart"
                Log.d("CartItems", "Home fragment observed cart size: $itemSize")
                if (itemSize > 0) {
                    cartFab.visibility = View.VISIBLE
                } else {
                    cartFab.visibility = View.GONE
                }
            }
        }
    }

    private fun searchItems(query: String?) {
        val items: ArrayList<FurnitureItem> = ArrayList()
        items.clear()

        val observer = Observer<List<FurnitureItem>> {
            for (i in it) {
                if (i.name == query || query?.let { it1 ->
                        i.description.contains(
                            it1,
                            ignoreCase = true
                        )
                    } == true) {
                    items.add(i)
                }
            }
            if (items.isNotEmpty()) adapter.submitList(items)

            Log.d(AddItemFragment.TAG, "Number of items in adapter search: ${adapter.itemCount}}")

        }
        vm.items.observe(viewLifecycleOwner, observer)

    }

    override fun onStart() {
        val observer = Observer<List<FurnitureItem>> {
            adapter.submitList(it.sortedByDescending { it.uploadTime })
            Log.d(AddItemFragment.TAG, "Number of items in adapter: ${adapter.itemCount}}")

        }
        vm.items.observe(viewLifecycleOwner, observer)
        super.onStart()
    }
}