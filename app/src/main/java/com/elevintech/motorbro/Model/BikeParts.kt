package com.elevintech.motorbro.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

@Parcelize
class BikeParts(
    var id: String = "",
    var userId: String = "",
    var date: String = "",
    var dateLong: Long = 0.toLong(),
    var odometer: Double = 0.0,
    var typeOfParts: String = "",
    var brand: String = "",
    var note: String = "",
    var price: Double = 0.0,
    var createdByShop: Boolean = false) : Parcelable
