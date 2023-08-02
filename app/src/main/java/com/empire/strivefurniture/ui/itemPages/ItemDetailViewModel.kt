package com.empire.strivefurniture.ui.itemPages

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empire.strivefurniture.models.FurnitureItem
import com.empire.strivefurniture.utils.FirebaseUtils
import com.empire.strivefurniture.utils.FirebaseUtils.CART
import com.empire.strivefurniture.utils.FirebaseUtils.currentUser
import com.empire.strivefurniture.utils.FirebaseUtils.usersDatabasePath
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class ItemDetailViewModel : ViewModel() {
    private val _cartItems = MutableLiveData<List<FurnitureItem>>()
    val cartItems: LiveData<List<FurnitureItem>> get() = _cartItems

    init {
        getCartItems()
    }


    private fun getCartItems() {
        Log.d("CartItems", "Getting cart items")
        viewModelScope.launch {
            if (currentUser != null){
                usersDatabasePath.child(currentUser!!.uid.toString()).child(CART)
                    .addValueEventListener(object : ValueEventListener {
                        val cartedItems = ArrayList<FurnitureItem>()
                        override fun onDataChange(snapshot: DataSnapshot) {
                            cartedItems.clear()
                            Log.d("CartItems", "Cart item snapshot size: ${snapshot.childrenCount}")

                            val items = snapshot.children
                            for (i in items) {
                                i.getValue(FurnitureItem::class.java)?.let { cartedItems.add(it) }
                            }
                            if (snapshot.childrenCount.toInt() == cartedItems.size) _cartItems.value =
                                cartedItems
                            Log.d("CartItems", "Exposed cart items: ${_cartItems.value}")
                            Log.d("CartItems", "Exposed cart items size: ${_cartItems.value?.size}")

                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
            }
        }
    }

    fun removeCartItem(item: FurnitureItem): Boolean {
        Log.d("CartItems", "Cart item removed: ${item.name}")
        return  usersDatabasePath.child(currentUser!!.uid.toString()).child(CART).child(item.id)
            .removeValue().isComplete
    }

    fun addCartItem(item: FurnitureItem): Boolean {
        Log.d("CartItems", "Cart item added: ${item.name}")
        return usersDatabasePath.child(currentUser!!.uid.toString()).child(CART).child(item.id)
            .setValue(item).isComplete
    }

    fun addItemToCarted(item: FurnitureItem, sellerId: String){
        usersDatabasePath.child(sellerId).child(FirebaseUtils.MY_CARTED).child(item.id)
            .setValue(item).isComplete
    }

    fun removeItemFromCarted(item: FurnitureItem, sellerId: String){
        usersDatabasePath.child(sellerId).child(FirebaseUtils.MY_CARTED).child(item.id)
            .removeValue().isComplete
    }
}