package com.elevintech.motorbro.Model

import java.io.Serializable

class Shop (
    var name: String = "",
    var description: String = "",
    var imageUrl: String = "",
    var dateEstablished: String = "",
    var address: String = ""
) : Serializable