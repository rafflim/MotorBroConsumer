package com.elevintech.motorbro.Model

class ShopProduct {
    var userId: String = ""
    var productId: Int = 0
    var mainImage: String = ""
    var title: String = ""
    var price: Double = 0.0


    constructor(userId: String, productId: Int, mainImage: String, title: String, price: Double ) {
        this.userId = userId
        this.productId = productId
        this.mainImage = mainImage
        this.title = title
        this.price = price

    }
}