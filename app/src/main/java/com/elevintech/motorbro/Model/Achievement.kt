package com.elevintech.motorbro.Model

import java.io.Serializable

class Achievement (

    var name: String  = "",
    var isAchieved: Boolean = false,
    var progress: Int = 0,
    var max: Int = 0

): Serializable {

    object Names {
        val FIRST_MONITOR = "firstMonitor"
        val FIRST_ODOMETER = "firstOdometer"
        val FIRST_PART_SERVICE = "firstPartService"
        val FIRST_FUEL = "firstFuel"
    }
}