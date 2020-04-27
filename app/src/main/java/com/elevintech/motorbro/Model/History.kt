package com.elevintech.motorbro.Model

import java.io.Serializable


class History (
    var id: String = "",
    var userId: String = "",
    var dateLong: Long = 0.toLong(),
    var typeOfHistory: String = "",
    var itemId:String  = "",
    var bikeId: String = "",
    var value:String  = "" /* displays the bike part name, refueling type or odometer distance*/): Serializable
