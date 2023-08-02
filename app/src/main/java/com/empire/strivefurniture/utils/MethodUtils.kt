package com.empire.strivefurniture.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import com.empire.strivefurniture.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

object MethodUtils {
    fun makePhoneCall(contact: String, context: Context) {
        val intent = Intent().apply {
            action = Intent.ACTION_CALL
            data = Uri.parse("tel:$contact")
        }
        context.startActivity(intent)
    }

    fun formatCurrency(numberString: String): String {
        val number = numberString.toLongOrNull()
            ?: return numberString // Return the original string if parsing fails

        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        if (numberFormat is DecimalFormat) {
            numberFormat.applyPattern("#,###")
        }

        return numberFormat.format(number)
    }

    fun showDialog(context: Context, title: String, message: String, navController: NavController) {
        val dialog = MaterialAlertDialogBuilder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(
                "Login"
            ) { dialog, which ->
                navController.navigate(R.id.action_global_loginFragment)
            }
            setNegativeButton("Register") { dialog, _ ->
                navController.navigate(R.id.action_global_registerFragment)
            }
        }

        dialog.show()

    }
}