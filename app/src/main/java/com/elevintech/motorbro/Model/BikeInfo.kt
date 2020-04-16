package com.elevintech.motorbro.Model

import java.io.Serializable

class BikeInfo():Serializable {
    var id: String = ""
    var userId: String = ""
    var brand: String = ""
    var model: String = ""
    var plateNumber: String = ""
    var odometer: String = ""
    var nickname: String = ""
    var fuelLiter: Double = 0.0
    var odometerValue: Double = 0.0
    var income: Double = 0.0
    var yearBought: String = ""
    var imageUrl: String = ""
    var bikeId: String = ""
    var deleted: Boolean = false
    var primary: Boolean = false
}