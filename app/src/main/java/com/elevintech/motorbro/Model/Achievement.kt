package com.elevintech.motorbro.Model

import java.io.Serializable

class Achievement (

    var name: String  = "",
    var isAchieved: Boolean = false,
    var progress: Int = 0

): Serializable {

    object Names {
        val FIRST_MONITOR = "firstMonitor"
        val FIRST_ODOMETER = "firstOdometer"
        val FIRST_PART_SERVICE = "firstPartService"
        val FIRST_FUEL = "firstFuel"

        val DISTANCE_TRAVELED = "distanceTraveled"
        val REFUEL_TIMES = "refuelTimes"
        val ADD_PART_SERVICE = "addPartService"
        val USER_DURATION = "userDuration"
    }
}