package com.empire.strivefurniture.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.empire.strivefurniture.models.FurnitureItem
import com.empire.strivefurniture.models.User
import com.empire.strivefurniture.ui.itemPages.AddItemFragment
import com.empire.strivefurniture.utils.FirebaseUtils
import com.empire.strivefurniture.utils.FirebaseUtils.furnitureItemDatabasePaths
import com.empire.strivefurniture.utils.FirebaseUtils.usersDatabasePath
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {
    val currentUser = FirebaseAuth.getInstance().currentUser
    private val _cartItems = MutableLiveData<List<FurnitureItem>>()
    val cartItems: LiveData<List<FurnitureItem>> get() = _cartItems

    private var _user = MutableLiveData<User?>()
    val user: MutableLiveData<User?> get() = _user

    private var _items = MutableLiveData<List<FurnitureItem>>()
    val items: MutableLiveData<List<FurnitureItem>> get() = _items

    private var _myCartedItems = MutableLiveData<Int>()
    val myCartedItems: MutableLiveData<Int> get() = _myCartedItems

    private var _mySoldItems = MutableLiveData<Int>()
    val mySoldItems: MutableLiveData<Int> get() = _mySoldItems

    init {
        getCartItems()
        getSellerCartedItems()
        getSellerSoldItems()
        getUser()
        getFurnitureItems()
    }

    private fun getUser() {
        if (currentUser != null) {
            usersDatabasePath
                .child(currentUser.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d("User", "User: ${snapshot.value}")
                        val currentUser = snapshot.getValue(User::class.java)
                        _user.value = currentUser
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }

    }

    private fun getFurnitureItems() {
        furnitureItemDatabasePaths.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val fItems = mutableListOf<FurnitureItem>()

                for (productSnapshot in snapshot.children) {
                    val id = productSnapshot.key
                    val name = productSnapshot.child("name").getValue(String::class.java)
                    val price = productSnapshot.child("price").getValue(String::class.java)
                    val itemQty = productSnapshot.child("itemQty").getValue(String::class.java)
                    val location = productSnapshot.child("location").getValue(String::class.java)
                    val seller = productSnapshot.child("seller").getValue(String::class.java)
                    val description = productSnapshot.child("description").getValue(String::class.java)
                    val contact = productSnapshot.child("contact").getValue(String::class.java)
                    val sellerName = productSnapshot.child("sellerName").getValue(String::class.java)
                    val uploadTime = productSnapshot.child("uploadTime").getValue(String::class.java)

                    val imagesSnapshot = productSnapshot.child("photo")
                    val imageUrls = arrayListOf<String>()

                    for (imageSnapshot in imagesSnapshot.children) {
                        val imageUrl = imageSnapshot.getValue(String::class.java)
                        if (imageUrl != null) {
                            imageUrls.add(imageUrl)
                        }
                    }


                    if (id != null && name != null && price != null && itemQty != null && location != null && seller != null &&
                        description != null && contact != null && sellerName != null && uploadTime != null) {
                        val furnitureItem = FurnitureItem(
                            id, name, price, itemQty, location, seller, description,
                            imageUrls, contact, sellerName, uploadTime
                        )
                        fItems.add(furnitureItem)
                    }
                }

                _items.value = fItems
                Log.d(AddItemFragment.TAG, "Emitted Items list size: ${fItems.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle cancellation
            }
        })
    }






    private fun getCartItems() {
        Log.d("CartItems", "Gettting cart items")
        viewModelScope.launch {
            if (currentUser != null) {
                usersDatabasePath.child(currentUser!!.uid).child(FirebaseUtils.CART)
                    .addValueEventListener(object : ValueEventListener {
                        val cartedItems = ArrayList<FurnitureItem>()
                        override fun onDataChange(snapshot: DataSnapshot) {
                            cartedItems.clear()
                            Log.d("CartItems", "Cart snapshot size: ${snapshot.childrenCount}")
                            val items = snapshot.children
                            for (i in items) {
                                i.getValue(FurnitureItem::class.java)?.let { cartedItems.add(it) }
                            }
                            if (snapshot.childrenCount.toInt() == cartedItems.size) _cartItems.value =
                                cartedItems
                            Log.d("CartItems", "Empty cartedItems size: ${cartedItems.size}")
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
            }
        }
    }

    fun clearCartItems(): Boolean {
        return usersDatabasePath.child(currentUser!!.uid).child(FirebaseUtils.CART)
            .removeValue().isComplete
    }

    private fun getSellerSoldItems() {
        if (currentUser != null) {
            usersDatabasePath.child(currentUser!!.uid).child(FirebaseUtils.MY_SOLD)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val number = snapshot.childrenCount
                        _mySoldItems.value = number.toInt()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
    }

    private fun getSellerCartedItems() {
        if (currentUser != null) {

            usersDatabasePath.child(currentUser.uid).child(FirebaseUtils.MY_CARTED)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val number = snapshot.childrenCount
                        _myCartedItems.value = number.toInt()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
    }

    fun addItemToCarted(item: FurnitureItem) {
        if (currentUser != null) {
            usersDatabasePath.child(currentUser.uid).child(FirebaseUtils.MY_CARTED).child(item.id)
                .setValue(item).isComplete
        }
    }

    fun removeItemFromCarted(item: FurnitureItem) {
        currentUser?.let {
            usersDatabasePath.child(it.uid).child(FirebaseUtils.MY_CARTED).child(item.id)
                .removeValue().isComplete
        }
    }

}