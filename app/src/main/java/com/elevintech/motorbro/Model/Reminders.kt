package com.elevintech.motorbro.Model

class Reminders {
    var userId: String = ""
    var title: String = ""
    var startDate: Double = 0.0
    var endDate: Double = 0.0


    constructor(userId: String, title: String, startDate: Double, endDate: Double ) {
        this.userId = userId
        this.title = title
        this.startDate = startDate
        this.endDate = endDate

    }
}