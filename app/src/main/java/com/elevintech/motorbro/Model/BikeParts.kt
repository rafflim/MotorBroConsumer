package com.elevintech.motorbro.Model

class BikeParts {
    var userId: String = ""
    var date: Double = 0.0
    var odometer: Double = 0.0
    var typeOfParts: String = ""
    var brand: String = ""
    var price: Double = 0.0

    constructor(userId: String, date: Double, odometer: Double, typeOfParts: String, brand: String, price: Double) {
        this.userId = userId
        this.date = date
        this.odometer = odometer
        this.typeOfParts = typeOfParts
        this.brand = brand
        this.price = price

    }
}