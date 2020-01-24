package com.elevintech.motorbro.Model

class User(var firstName: String = "",
           var lastName: String = "",
           var gender: String = "",
           var email: String = "",
           var customParts: List<String> = listOf(),
           var customReminders: List<String> = listOf(),
           var customHistory: List<String> = listOf())