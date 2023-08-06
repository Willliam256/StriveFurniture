package com.empire.strivefurniture.ui.profilePage

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.empire.strivefurniture.R
import com.empire.strivefurniture.adapters.PersonalItemsAdapter
import com.empire.strivefurniture.databinding.FragmentProfileBinding
import com.empire.strivefurniture.models.FurnitureItem
import com.empire.strivefurniture.models.User
import com.empire.strivefurniture.ui.itemPages.AddItemFragment
import com.empire.strivefurniture.utils.FirebaseUtils.profileImagePaths
import com.empire.strivefurniture.utils.FirebaseUtils.usersDatabasePath
import com.empire.strivefurniture.viewModel.AppViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val vm: AppViewModel by activityViewModels()
    private val adapter: PersonalItemsAdapter = PersonalItemsAdapter()
    private var user = User()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.apply {
            logoutButton.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
            }
            editProfileButton.setOnClickListener {
                findNavController().navigate(
                    R.id.action_profileFragment_to_editProfileFragment,
                    bundleOf("user" to user)
                )
            }
            editAvatar.setOnClickListener {
                getImage.launch("image/*")
            }
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            addItemFab.setOnClickListener { findNavController().navigate(R.id.action_profileFragment_to_addItemFragment) }
            vm.user.observe(requireActivity()) {
                if (it != null) {
                    name.text = it.name
                    location.text = it.location
                    phone.text = it.phoneNumber
                    email.text = it.email
                    user = it
                    val context = binding.root.context
                    if (context != null) {
                        Glide.with(context).load(it.profileImage)
                            .fallback(R.drawable.baseline_account_circle_24).into(avatar)
                    }
                }
            }
            vm.items.observe(requireActivity()) {
                val listOfItems = ArrayList<FurnitureItem>()
                for (item in it) {
                    if (item.seller == user?.id) {
                        listOfItems.add(item)
                    }
                    adapter.submitList(listOfItems)
                    profileRv.adapter = adapter
                }
                Log.d(AddItemFragment.TAG, "Number of items in adapter ==>> ${adapter.itemCount}}")
                if (it.isEmpty()) {
                    noItemsText.visibility = View.VISIBLE
                    profileRv.visibility = View.GONE
                } else {
                    noItemsText.visibility = View.GONE
                    profileRv.visibility = View.VISIBLE
                }
            }
//            vm.myCartedItems.observe(requireActivity()) {
//                binding.cartedItems.text = it.toString()
//            }

            vm.items.observe(requireActivity()) {
                binding.cartedItems.text = it.size.toString()
            }
            vm.mySoldItems.observe(requireActivity()) {
                binding.soldItems.text = it.toString()
            }
        }
    }

    private var getImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                updateImage(uri)
            }
        }

    private fun updateImage(uri: Uri) {
        Log.d("MyLogsofImages", "Uploading")
        Toast.makeText(requireContext(), "Uploading...", Toast.LENGTH_SHORT).show()
        profileImagePaths.child(user!!.id).putFile(uri).addOnSuccessListener {

            it.storage.downloadUrl.addOnSuccessListener { imageLink ->
                Log.d("MyLogsofImages", "Image link: $imageLink")
                Log.d(
                    "MyLogsofImages",
                    "Path: ${usersDatabasePath}/${user.id}/profileImage/$imageLink"
                )

                try {
                    FirebaseDatabase.getInstance().getReference("Users").child(user!!.id)
                        .child("profileImage").setValue(imageLink.toString()).addOnSuccessListener {
                            Log.d("MyLogsofImages", "Success")
                            Toast.makeText(
                                requireContext(),
                                "Profile update successful",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }.addOnFailureListener {
                            Log.d("MyLogsofImages", "Error: ${it.message}")
                            Toast.makeText(
                                requireContext(),
                                "Profile update failed!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                } catch (e: Exception) {
                    Log.d("MyLogsofImages", "Error: ${e.message}")
                    Toast.makeText(
                        requireContext(),
                        "Profile update failed!",
                        Toast.LENGTH_LONG
                    )
                        .show()

                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
}