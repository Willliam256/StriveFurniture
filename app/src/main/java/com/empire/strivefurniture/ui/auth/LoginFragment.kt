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
import com.empire.strivefurniture.databinding.FragmentLoginBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.apply {
            loginBtn.setOnClickListener {
                if (checkFields()) authenticateUser()
            }
            registerBtn.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            forgotPassword.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_recoverPasswordFragment)
            }
        }
    }

    private fun authenticateUser() {
        binding.progressIndicator.visibility = View.VISIBLE
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
            binding.emailTv.text.toString(),
            binding.passwordTv.text.toString()
        )
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    Toast.makeText(requireActivity(), "Sign In Successful", Toast.LENGTH_LONG)
                        .show()
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    binding.progressIndicator.visibility = View.GONE
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "User doesn't exist. Please create an account.",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
                    binding.progressIndicator.visibility = View.GONE
                }
            }
    }

    private fun checkFields(): Boolean {
        binding.apply {
            emailV.error = null
            emailV.error = null
            if (TextUtils.isEmpty(emailTv.text.toString())) {
                emailTv.setText("")
                emailV.error = "Email address can't be empty"
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
        }
        return true
    }
}