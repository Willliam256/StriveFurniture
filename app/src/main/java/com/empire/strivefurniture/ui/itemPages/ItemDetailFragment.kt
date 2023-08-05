package com.empire.strivefurniture.ui.itemPages

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
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

    private lateinit var nextImage: ImageView
    private lateinit var previousImage: ImageView
    private lateinit var imageSwitcherDetail: ImageSwitcher
    var position = 0 //Current position of the selected Image

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemDetailBinding.inflate(layoutInflater)
        context = binding.root.context


        /**
         * Initializing the variables of the Image display for the selected images
         */
        nextImage = binding.nextImageId
        previousImage = binding.previousImageId

        // Initialize the ImageSwitcher
        imageSwitcherDetail = binding.detailImageSwitcherId
        imageSwitcherDetail.setFactory {
            val imageView = ImageView(requireContext())
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView
        }

        /**
         * Calling the initView Function to inialize all the items
         */
        initViews()
        /**
         * Set on next and previous btns click to navigate btn previous and next images selected
         */
        // Set next and previous buttons click listeners
        binding.nextImageId.setOnClickListener {
            if (position < item.photo.size - 1) {
                position++
                setImagesInImageSwitcher(item.photo, position)
            } else {
                binding.previousImageId.visibility = View.VISIBLE
                binding.nextImageId.visibility = View.GONE
                Toast.makeText(requireContext(), "Done", Toast.LENGTH_LONG).show()
            }
        }

        binding.previousImageId.setOnClickListener {
            if (position > 0) {
                position--
                setImagesInImageSwitcher(item.photo, position)
            } else {
                binding.previousImageId.visibility = View.GONE
                binding.nextImageId.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Done", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

    /**
     * Load image into the Image Switcher
     */
    private fun setImagesInImageSwitcher(imageUrls: List<String>, position: Int) {
        val imageUrl = imageUrls[position]
        loadImageToImageSwitcher(imageUrl)
    }


    private fun loadImageToImageSwitcher(imageUrl: String) {
        Glide.with(requireContext())
            .load(imageUrl)
            .into(imageSwitcherDetail.currentView as ImageView)
    }
    //End Loading Image to the Image Switcher



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

            setImagesInImageSwitcher(item.photo, position)
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