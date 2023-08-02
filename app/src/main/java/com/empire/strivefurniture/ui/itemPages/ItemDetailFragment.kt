package com.empire.strivefurniture.ui.itemPages

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.empire.strivefurniture.R
import com.empire.strivefurniture.databinding.FragmentItemDetailBinding
import com.empire.strivefurniture.models.FurnitureItem
import com.empire.strivefurniture.utils.MethodUtils.formatCurrency
import com.empire.strivefurniture.utils.MethodUtils.showDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ItemDetailFragment : Fragment() {
    private val vm: ItemDetailViewModel by activityViewModels()
    private lateinit var binding: FragmentItemDetailBinding
    private val args: ItemDetailFragmentArgs by navArgs()
    private lateinit var item: FurnitureItem
    private val auth = FirebaseAuth.getInstance()
    private var user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private lateinit var cartItems: List<FurnitureItem>
    private var context: Context? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemDetailBinding.inflate(layoutInflater)
        initViews()
        context = binding.root.context
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initViews() {
        binding.apply {
            vm.cartItems.observe(requireActivity()) { items ->
                cartItems = items
                val contxt = root.context
                Log.d("CartItems", "Observed cart items: ${items}")
                Log.d("CartItems", "Observed cart items size: ${items.size}")
                if (items.contains(item)) {
                    binding.addToCartButton.text = "Remove from cart"
                    if (contxt != null) binding.addToCartButton.icon =
                        contxt.getDrawable(R.drawable.baseline_remove_shopping_cart_24)

                } else {
                    binding.addToCartButton.text = "Add to cart"
                    if (contxt != null) binding.addToCartButton.icon =
                        contxt.getDrawable(R.drawable.baseline_shopping_cart_24)
                }
            }

            addToCartButton.setOnClickListener {
                if (user != null) {
                    if (cartItems.contains(item)) {
                        vm.removeCartItem(item)
                        vm.removeItemFromCarted(item, item.seller)
                    } else {
                        vm.addCartItem(item)
                        vm.addItemToCarted(item, item.seller)
                    }
                } else {
                    showDialog(
                        requireContext(),
                        "Sign in required",
                        "In order to enhance your shopping experience, we kindly request you to sign in to your account before adding items to your cart. This will enable you to seamlessly manage your selections and proceed with a smooth checkout process.",
                        findNavController()
                    )
                }

            }


            if (auth.currentUser != null) {
                if (auth.currentUser?.uid == args.item.seller) {
                    editProductButton.visibility = View.VISIBLE
                }
            }

            editProductButton.setOnClickListener {
                findNavController().navigate(
                    R.id.action_itemDetailFragment_to_addItemFragment,
                    bundleOf("isEditing" to true, "item" to item)
                )
            }

            toolbar.setOnClickListener {
                toolbar.setOnClickListener {
                    findNavController().navigateUp()
                }
            }

            Glide.with(root.context).load(item.photo).into(image)
            name.text = item.name
            price.text = formatCurrency(item.price) + " UGX"
            description.text = item.description
            sellerLocation.text = item.location
            sellerName.text = item.sellerName
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = args.item
        user = auth.currentUser
    }
}