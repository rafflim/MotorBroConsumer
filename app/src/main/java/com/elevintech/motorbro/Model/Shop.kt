package com.elevintech.motorbro.Model

import java.io.Serializable

class Shop (
    var name: String = "",
    var spotlight: Boolean = false,
    var description: String = "",
    var imageUrl: String = "",
    var dateEstablished: String = "",
    var address: String = "",
    var shopId: String = "",
    var searchTags: List<String> = listOf(),
    var deviceTokens: Map<String, String> = mapOf() // list of the device tokens of users who work for the shop (e.g. the shop owner and employees) used for sending fcm notifications
) : Serializable