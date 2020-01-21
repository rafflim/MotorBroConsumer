package com.elevintech.motorbro.Model


class History {
    var userId: String = ""
    var title: String = ""
    var kilometers: String = ""
    var date: Double = 0.0
    var price: String = ""


    constructor(userId: String, title: String, kilometers: String, date: Double, price: String) {
        this.userId = userId
        this.title = title
        this.kilometers = kilometers
        this.date = date
        this.price = price

    }
}