package com.empire.strivefurniture.ui.auth

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.empire.strivefurniture.R
import com.empire.strivefurniture.databinding.FragmentRegisterBinding
import com.empire.strivefurniture.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.apply {
            toolbar.setNavigationOnClickListener { findNavController().navigateUp() }
            signinBtn.setOnClickListener {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
            signupBtn.setOnClickListener {
                if (checkFields()) signupUser()
            }
        }
    }

    private fun signupUser() {
        binding.apply {
            val name = nameTv.text.toString()
            val email = emailTv.text.toString()
            val phone = phoneTv.text.toString()
            val location = locationTv.text.toString()
            val password = passwordTv.text.toString()
            val authInstance = FirebaseAuth.getInstance()
            binding.progressIndicator.visibility = View.VISIBLE
            authInstance.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task: Task<AuthResult?> ->
                    if (task.isSuccessful) {
                        val user = authInstance.currentUser
                        if (user != null) {
                            val person = User(user.uid, name, email, phone, location, "")
                            FirebaseDatabase.getInstance().reference.child("Users").child(user.uid)
                                .setValue(person)
                            Toast.makeText(
                                requireActivity(),
                                "Welcome $name, Sign Up Success",
                                Toast.LENGTH_LONG
                            ).show()
                            findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                            binding.progressIndicator.visibility = View.GONE
                        }
                    } else {
                        binding.progressIndicator.visibility = View.GONE
                        showError(task.exception!!.message.toString())
                    }
                }
        }
    }

    private fun checkFields(): Boolean {
        binding.nameV.error = null
        binding.emailV.error = null
        binding.passwordV.error = null
        if (TextUtils.isEmpty(binding.emailTv.text.toString())) {
            binding.emailTv.setText("")
            binding.emailV.error = "Email address can't be empty"
            return false
        }
        if (TextUtils.isEmpty(binding.nameTv.text.toString())) {
            binding.nameTv.setText("")
            binding.nameV.error = "Name address can't be empty"
            return false
        }
        if (!binding.emailTv.text.toString().contains("@")) {
            binding.emailV.error = "User Email is Invalid"
            return false
        }
        if (TextUtils.isEmpty(binding.passwordTv.text.toString())) {
            binding.passwordTv.setText("")
            binding.passwordV.error = "password cant be empty"
            return false
        }
        return true
    }

    private fun showError(errorCode: String) {
        var error: String? = null
        when (errorCode) {
            "ERROR_INVALID_CREDENTIAL" -> error =
                "The supplied auth credential is malformed or has expired."
            "ERROR_INVALID_EMAIL" -> {
                error = "The email address is badly formatted."
                binding.emailTv.error = error
                binding.emailTv.requestFocus()
            }
            "ERROR_WRONG_PASSWORD" -> {
                error = "The password is incorrect."
                binding.passwordV.error = error
                binding.passwordTv.requestFocus()
                binding.passwordTv.setText("")
            }
            "ERROR_USER_MISMATCH" -> error =
                "The supplied credentials do not correspond to the previously signed in user."
            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                error = "The email address is already in use by another account."
                binding.emailV.error = error
                binding.emailTv.requestFocus()
            }
            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> error =
                "This credential is already associated with a different user account."
            "ERROR_USER_DISABLED" -> error =
                "The user account has been disabled by an administrator."
            "ERROR_USER_TOKEN_EXPIRED", "ERROR_INVALID_USER_TOKEN" -> error =
                "The user\\'s credential is no longer valid. The user must sign in again."
            "ERROR_USER_NOT_FOUND" -> error =
                "There is no user record corresponding to this identifier. The user may have been deleted."
            "ERROR_OPERATION_NOT_ALLOWED" -> error =
                "This operation is not allowed. You must enable this service in the console."
            "ERROR_WEAK_PASSWORD" -> {
                error = "The given password is invalid."
                binding.passwordV.error = "The password is invalid it must 6 characters at least"
                binding.passwordTv.requestFocus()
            }
        }
        Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show()
    }
}