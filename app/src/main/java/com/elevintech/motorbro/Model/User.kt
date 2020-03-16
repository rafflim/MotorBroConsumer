package com.elevintech.motorbro.Model

class User(var uid: String = "",
            var firstName: String = "",
           var lastName: String = "",
           var gender: String = "",
           var email: String = "",
           var usersRegistrationProgress: Int = 0,
           var profileImage: String = "",
           var customParts: List<String> = listOf(),
           var customReminders: List<String> = listOf(),
           var customHistory: List<String> = listOf(),
           var customFuel: List<String> = listOf(),
           var userType: String = "",
           var favoriteShops: List<String> = listOf()
)