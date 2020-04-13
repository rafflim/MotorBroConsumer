package com.elevintech.motorbro.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Refueling (
    var id: String = "",
    var userId: String = "",
    var title: String = "",
    var kilometers: Double = 0.00,
    var date: String = "",
    var dateLong: Long = 0.toLong(),
    var typeOfFuel: String = "",
    var note: String = "",

    var pricePerGallon: Double = 0.00,
    var totalCost: Double = 0.00,
    var priceGallons: Double = 0.00,

    var location: String = ""
) : Parcelable