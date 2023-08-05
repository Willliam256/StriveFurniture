package com.empire.strivefurniture.utils

import android.util.Log
import com.empire.strivefurniture.models.FurnitureItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

object FirebaseUtils {
    const val CART = "cart"

    const val MY_CARTED = "my_carted"
    const val MY_SOLD = "my_sold"

    val currentUser = FirebaseAuth.getInstance().currentUser

    val furnitureItemStoragePaths = FirebaseStorage.getInstance().getReference("Items")

    val furnitureItemDatabasePaths = FirebaseDatabase.getInstance().getReference("Items")

    val profileImagePaths = FirebaseStorage.getInstance().getReference("Avatars")

    val usersDatabasePath = FirebaseDatabase.getInstance().getReference("Users")
//    fun deleteItem(item: FurnitureItem) {
//        furnitureItemDatabasePaths.child(item.id).removeValue().addOnCompleteListener {
//            if (it.isSuccessful) furnitureItemStoragePaths.child(item.photo).delete()
//        }
//    }

    fun removeCartItem(item: FurnitureItem): Boolean {
        Log.d("CartItems", "Cart item removed: ${item.name}")
        return if (currentUser != null) {
            usersDatabasePath.child(currentUser.uid).child(CART).child(item.id)
                .removeValue().isComplete
        } else false
    }

    fun addItemsToSold(item: FurnitureItem, sellerId: String): Boolean {
        return usersDatabasePath.child(sellerId).child(MY_SOLD)
            .child(item.id)
            .setValue(item).isComplete
    }
}