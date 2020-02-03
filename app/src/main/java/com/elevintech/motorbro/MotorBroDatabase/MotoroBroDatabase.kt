package com.elevintech.motorbro.MotorBroDatabase

import android.net.Uri
import com.elevintech.motorbro.Model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class MotoroBroDatabase {

    fun getUser(callback: (User) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("users").document(uid)

        docRef.get().addOnSuccessListener { documentSnapshot ->

            var user = User()

            if (documentSnapshot != null && documentSnapshot.exists()) {
                user = documentSnapshot.toObject(User::class.java)!!

            }

            callback( user )
        }
    }




    fun getUserBike(callback: (BikeInfo) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("bikes").document(uid)

        docRef.get().addOnSuccessListener { documentSnapshot ->

            var bike = BikeInfo()

            if (documentSnapshot != null && documentSnapshot.exists()) {
                bike = documentSnapshot.toObject(BikeInfo::class.java)!!

            }

            callback( bike )
        }
    }



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

    fun updateBikeInfo(bikeInfo: BikeInfo, callback: () -> Unit) {
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

        db.collection("users").document(FirebaseAuth.getInstance().uid!!).collection("bike-parts")
            .document()
            .set(bikeParts)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                    e -> println(e)
                callback()
            }
    }

    fun saveHistory(history: History, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(FirebaseAuth.getInstance().uid!!).collection("history")
            .document()
            .set(history)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                    e -> println(e)
                callback()
            }
    }

    fun saveReminder(reminder: Reminders, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(FirebaseAuth.getInstance().uid!!).collection("reminders")
            .document()
            .set(reminder)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                    e -> println(e)
                callback()
            }
    }

    fun saveCustomPart(part: String, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val userBio = db.collection("users").document(uid)

        userBio
            .update("customParts", FieldValue.arrayUnion("$part"))
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }
    }

    fun saveCustomReminder(reminder: String, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val userBio = db.collection("users").document(uid)

        userBio
            .update("customReminders", FieldValue.arrayUnion("$reminder"))
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }
    }

    fun saveCustomHistory(history: String, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val userBio = db.collection("users").document(uid)

        userBio
            .update("customHistory", FieldValue.arrayUnion("$history "))
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }
    }

    fun saveCustomFuel(fuel: String, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val userBio = db.collection("users").document(uid)

        userBio
            .update("customFuel", FieldValue.arrayUnion("$fuel "))
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }
    }

    fun getUserReminders(callback: (MutableList<Reminders>) -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("users").document(uid).collection("reminders")
        val list = mutableListOf<Reminders>()

        docRef.get()
            .addOnSuccessListener {

                for (reminder in it){
                    val reminder = reminder.toObject(Reminders::class.java)
                    list.add(reminder)
                }

                callback(list)

        }

    }

    fun getShops(callback: (MutableList<Shop>) -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("shops")
        val list = mutableListOf<Shop>()

        docRef.get()
            .addOnSuccessListener {

                for (minShop in it){
                    val shop = minShop.toObject(Shop::class.java)
                    list.add(shop)
                }

                callback(list)

            }

    }

    fun getUserHistory(callback: (MutableList<History>) -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("users").document(uid).collection("history")
        val list = mutableListOf<History>()

        docRef.get()
            .addOnSuccessListener {

                for (history in it){
                    val reminder = history.toObject(History::class.java)
                    list.add(reminder)
                }

                callback(list)

            }

    }

    fun getUserBikeParts(callback: (MutableList<BikeParts>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("users").document(uid).collection("bike-parts")
        val list = mutableListOf<BikeParts>()

        docRef.get()
            .addOnSuccessListener {
                for (bikePart in it){
                    val bikePart = bikePart.toObject(BikeParts::class.java)
                    list.add(bikePart)
                }

                callback(list)
            }
    }

    fun getUserRefueling(callback: (MutableList<Refueling>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("users").document(uid).collection("refueling")
        val list = mutableListOf<Refueling>()

        docRef.get()
            .addOnSuccessListener {
                for (refuel in it){
                    val refuel = refuel.toObject(Refueling::class.java)
                    list.add(refuel)
                }

                callback(list)

            }
    }

    fun getUserOdometers(callback: (MutableList<OdometerUpdate>) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("users").document(uid).collection("odometerUpdate")

        val list = mutableListOf<OdometerUpdate>()

        docRef.get()
            .addOnSuccessListener {

                for (odo in it){
                    val odometer = odo.toObject(OdometerUpdate::class.java)
                    list.add(odometer)
                }

                callback(list)

            }
    }


    fun saveRefueling(refueling: Refueling, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(FirebaseAuth.getInstance().uid!!).collection("refueling")
            .document()
            .set(refueling)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                    e -> println(e)
                callback()
            }
    }

    fun saveOdometerUpdate(odometerUpdate: OdometerUpdate, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(FirebaseAuth.getInstance().uid!!).collection("odometerUpdate")
            .document()
            .set(odometerUpdate)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                    e -> println(e)
                callback()
            }
    }

    fun uploadDocumentsToFirebaseStorage(imageUri: Uri, callback: (url: String) -> Unit) {

        var filename = UUID.randomUUID().toString()
        var storageRef = FirebaseStorage.getInstance().getReference("/documents/$filename.jpg")

        // UPLOAD TO FIREBASE
        storageRef.putFile(imageUri)
            .addOnSuccessListener {

                storageRef.downloadUrl.addOnSuccessListener {
                    var url = it.toString()

                    callback(url)
                }

            }
            .addOnFailureListener{
                println( it.toString())
            }
    }

    // TODO: Save the insurance model here
    fun saveInsuranceDocuments(insurance: Insurance, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val insuranceDocument = db.collection("users").document(uid).collection("documents").document("insurance")

        insuranceDocument
            .set(insurance)
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }
    }

    fun saveDriversLicenseDocument(license: DriversLicense, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val licenseDocument = db.collection("users").document(uid).collection("documents").document("drivers-license")

        licenseDocument
            .set(license)
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }
    }




    fun saveFrontInsuranceImageDocument(url: String, callback: () -> Unit){

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val insuranceDocument = db.collection("users").document(uid).collection("documents").document("insurance")

        insuranceDocument
            .update("insuranceFrontImage", FieldValue.arrayUnion("$url"))
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }

    }

    fun saveBackInsuranceImageDocument(url: String, callback: () -> Unit){

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val insuranceDocument = db.collection("users").document(uid).collection("documents").document("insurance")

        insuranceDocument
            .update("insuranceBackImage", FieldValue.arrayUnion("$url"))
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }

    }

    fun getInsuranceDocument(callback: (Insurance?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val insuranceDocument = db.collection("users").document(uid).collection("documents").document("insurance")

        insuranceDocument.get()
            .addOnSuccessListener {


                var insurance: Insurance? = null

                if (it != null && it.exists()) {
                    insurance = it.toObject(Insurance::class.java)!!

                }

                callback( insurance )

            }
    }

    fun getLicenseDocument(callback: (DriversLicense?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val document = db.collection("users").document(uid).collection("documents").document("drivers-license")

        document.get()
            .addOnSuccessListener {


                var license: DriversLicense? = null

                if (it != null && it.exists()) {
                    license = it.toObject(DriversLicense::class.java)!!

                }

                callback( license )

            }
    }
}