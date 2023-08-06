package com.empire.strivefurniture.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FurnitureItem(
    val id: String,
    val name: String,
    val price: String,
    val itemQty: String,
    val location: String,
    val seller: String,
    val description: String,
    val photo: ArrayList<String>,
    val contact: String,
    val sellerName: String,
    val uploadTime: String
) : Parcelable {
    constructor() : this("", "", "", "", "", "", "", arrayListOf(""), "", "", "")
}
