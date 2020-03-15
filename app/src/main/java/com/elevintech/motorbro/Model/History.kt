package com.elevintech.motorbro.Model

import java.io.Serializable


class History (
    var userId: String = "",
    var dateLong: Long = 0.toLong(),
    var typeOfHistory: String = "",
    var itemId:String  = "",
    var value:String  = "" /* displays the bike part name, refueling type or odometer distance*/): Serializable
