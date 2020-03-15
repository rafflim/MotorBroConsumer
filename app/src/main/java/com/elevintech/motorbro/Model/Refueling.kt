package com.elevintech.motorbro.Model

class Refueling (
    var userId: String = "",
    var title: String = "",
    var kilometers: Double = 0.00,
    var date: String = "",
    var dateLong: Long = 0.toLong(),
    var typeOfFuel: String = "",

    var pricePerGallon: Double = 0.00,
    var totalCost: Double = 0.00,
    var priceGallons: Double = 0.00,

    var location: String = ""
)