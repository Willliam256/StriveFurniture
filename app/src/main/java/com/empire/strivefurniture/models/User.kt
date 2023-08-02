package com.empire.strivefurniture.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val location: String = "",
    val profileImage: String = ""
) : Parcelable