package com.empire.strivefurniture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.empire.strivefurniture.adapters.CartItemAdapter
import com.empire.strivefurniture.databinding.FragmentCartBinding
import com.empire.strivefurniture.viewModel.AppViewModel

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private val adapter: CartItemAdapter = CartItemAdapter()
    private val vm: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }

    private fun initViews() {
        vm.cartItems.observe(requireActivity()) {
            adapter.submitList(it)
            binding.recyclerView.adapter = adapter
        }
        binding.apply {
            clearCart.setOnClickListener {
                val clearCart = vm.clearCartItems()
                if (clearCart) {
                    Toast.makeText(requireContext(), "Cart cleared!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

}