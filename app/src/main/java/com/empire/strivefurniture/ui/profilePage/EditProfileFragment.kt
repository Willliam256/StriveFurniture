package com.empire.strivefurniture.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.empire.strivefurniture.R
import com.empire.strivefurniture.databinding.FragmentEditProfileBinding
import com.empire.strivefurniture.models.User
import com.empire.strivefurniture.utils.FirebaseUtils.usersDatabasePath
import com.google.firebase.auth.FirebaseAuth

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private val args: EditProfileFragmentArgs by navArgs()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            etName.setText(args.user.name)
            etPhone.setText(args.user.phoneNumber)
            etLocation.setText(args.user.location)

            resetPsButton.setOnClickListener {
                auth.sendPasswordResetEmail(args.user.email).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Password reset email has been sent. \n Please check your email ${args.user.email}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Something went wrong. ${it.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            updateProfileButton.setOnClickListener {
                updateProfileButton.isEnabled = false
                val name =
                    if (etName.text!!.isNotEmpty()) etName.text.toString() else args.user.name
                val phone =
                    if (etPhone.text!!.isNotEmpty()) etPhone.text.toString() else args.user.phoneNumber
                val location =
                    if (etLocation.text!!.isNotEmpty()) etLocation.text.toString() else args.user.location

                updateUserInfo(
                    User(
                        id = args.user.id,
                        name = name,
                        email = args.user.email,
                        phoneNumber = phone,
                        location = location,
                        profileImage = args.user.profileImage
                    )
                )
            }
        }
    }

    private fun updateUserInfo(user: User) {
        usersDatabasePath.child(user.id).setValue(user).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(requireContext(), "Profile update successful", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
                binding.updateProfileButton.isEnabled = true

            }else{
                Toast.makeText(requireContext(), "Something wrong happened. ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                binding.updateProfileButton.isEnabled = true

            }
        }
    }
}