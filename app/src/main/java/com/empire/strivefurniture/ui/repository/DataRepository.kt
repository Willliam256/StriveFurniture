package com.empire.strivefurniture.ui.repository

import android.util.Log
import com.empire.strivefurniture.models.FurnitureItem
import com.empire.strivefurniture.ui.itemPages.AddItemFragment
import com.empire.strivefurniture.utils.FirebaseUtils.furnitureItemDatabasePaths
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

object DataRepository  {

    fun getFurnitureItems(): Flow<List<FurnitureItem>> = flow{
        furnitureItemDatabasePaths.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    val items = snapshot.children.mapNotNull { it.getValue(FurnitureItem::class.java) }
                    Log.d(AddItemFragment.TAG, "Items added: ${items.size}")

                    CoroutineScope(Dispatchers.IO).launch {
                        emit(items)
                        Log.d(AddItemFragment.TAG, "Emitted Items list size: ${items.size}")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}