package com.empire.strivefurniture.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.empire.strivefurniture.databinding.FragmentRecoverPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class RecoverPasswordFragment : Fragment() {
    private lateinit var binding: FragmentRecoverPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecoverPasswordBinding.inflate(layoutInflater)
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            sendButton.setOnClickListener {


                Log.d(
                    "Email",
                    "Email text ==> ${emailInput.text} \n Text lenght ==> ${emailInput.text?.length}"
                )


                if (emailInput.text!!.isNotBlank())
                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailInput.text.toString().trim()).addOnCompleteListener {
                        if (it.isSuccessful)
                            Toast.makeText(
                                requireContext(),
                                "Password reset email sent. Please check your email ${emailInput.text}",
                                Toast.LENGTH_SHORT
                            ).show()
                        else Toast.makeText(
                            requireContext(),
                            "Error, please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                else {
                    emailInput.error = "This is not a valid email."
                    Toast.makeText(
                        requireContext(),
                        "Email ==> ${emailInput.text}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}