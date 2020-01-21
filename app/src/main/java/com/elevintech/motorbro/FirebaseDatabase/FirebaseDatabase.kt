package com.elevintech.motorbro.FirebaseDatabase

import android.util.Log
import com.elevintech.motorbro.Model.BikeInfo
import com.elevintech.motorbro.Model.BikeParts
import com.elevintech.motorbro.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseDatabase {

    fun registerUser(user: User, callback: () -> Unit) {
        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()
    // Create a new user with a first and last name
//        val user = hashMapOf(
//            "first" to "Ada",
//            "last" to "Lovelace",
//            "born" to 1815
//        )

        // Add a new document with a generated ID
        db.collection("users").document(FirebaseAuth.getInstance().uid!!)
            .set(user)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                    e -> println(e)
                callback()
            }
//            .add(user)
//            .addOnSuccessListener { documentReference ->
//                println("Success")
//                callback()
//            }
//            .addOnFailureListener { e ->
//                println("Failure")
//                callback()
//            }

    }

    fun saveBikeInfo(bikeInfo: BikeInfo, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("bikes").document(FirebaseAuth.getInstance().uid!!)
            .set(bikeInfo)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                    e -> println(e)
                callback()
            }
    }

    fun saveBikeParts(bikeParts: BikeParts, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("bikeParts").document(FirebaseAuth.getInstance().uid!!)
            .set(bikeParts)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                    e -> println(e)
                callback()
            }
    }
}