package com.elevintech.motorbro.Model

import java.io.Serializable

class UserType(
    var uid: String = "",
    var userType: String = "customer",
    var token: String = ""

): Serializable {

    object Type {
        val OWNER = "owner"
        val EMPLOYEE = "employee"
        val CUSTOMER = "customer"
    }


}
