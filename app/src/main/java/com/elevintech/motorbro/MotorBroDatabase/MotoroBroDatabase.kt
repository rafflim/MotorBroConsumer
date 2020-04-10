package com.elevintech.motorbro.MotorBroDatabase

import android.net.Uri
import com.elevintech.motorbro.Model.*
import com.elevintech.motorbro.Utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.HashMap

class MotoroBroDatabase {

    fun getUserToken(callback: (String) -> Unit){
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("users").document(uid)

        docRef.get().addOnSuccessListener { documentSnapshot ->

            var user = UserType()

            if (documentSnapshot != null && documentSnapshot.exists()) {
                user = documentSnapshot.toObject(UserType::class.java)!!

            }

            callback( user.token )
        }
    }

    fun getUser(callback: (User) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("customers").document(uid)

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



    fun createUserType(callback: () -> Unit) {
        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val userType = UserType( uid )

        // Add a new document with a generated ID
        db.collection("users").document(uid)
            .set(userType)
            .addOnSuccessListener {

                callback()

            }
            .addOnFailureListener {
                e -> println(e)
                callback()
            }
    }

    fun createUser(customer: User, callback: () -> Unit){
        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()

        // Add a new document with a generated ID
        db.collection("customers").document(customer.uid)
            .set(customer)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                e -> println(e)
                callback()
            }
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

        db.collection("bikes").document(bikeInfo.bikeId)
            .set(bikeInfo)
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener {
                    e -> println(e)
                callback()
            }
    }

    fun saveBikeParts(bikeParts: BikeParts, callback: (String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("customers").document(FirebaseAuth.getInstance().uid!!).collection("bike-parts")
            .add(bikeParts)
            .addOnSuccessListener {
                callback(it.id)
            }
            .addOnFailureListener {
                    e -> println(e)
                callback(null)
            }
    }

    fun saveHistory(historyType: String, historyItemId: String, historyItemValue: Any, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()

        val history = History()
        history.userId = FirebaseAuth.getInstance().uid!!
        history.dateLong = Utils().getCurrentTimestamp()
        history.typeOfHistory = historyType
        history.itemId = historyItemId
        history.value = historyItemValue.toString()

        db.collection("customers").document(FirebaseAuth.getInstance().uid!!).collection("history")
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

        db.collection("customers").document(FirebaseAuth.getInstance().uid!!).collection("reminders")
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
        val userBio = db.collection("customers").document(uid)

        userBio
            .update("customParts", FieldValue.arrayUnion("$part"))
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }
    }

    fun saveCustomBrand(part: String, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val userBio = db.collection("customers").document(uid)

        userBio
            .update("customBrands", FieldValue.arrayUnion("$part"))
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }
    }

    fun saveCustomReminder(reminder: String, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val userBio = db.collection("customers").document(uid)

        userBio
            .update("customReminders", FieldValue.arrayUnion("$reminder"))
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }
    }

    fun saveCustomHistory(history: String, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val userBio = db.collection("customers").document(uid)

        userBio
            .update("customHistory", FieldValue.arrayUnion("$history "))
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }
    }

    fun saveCustomFuel(fuel: String, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val userBio = db.collection("customers").document(uid)

        userBio
            .update("customFuel", FieldValue.arrayUnion("$fuel "))
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }
    }

    fun getUserReminders(callback: (MutableList<Reminders>) -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("customers").document(uid).collection("reminders")
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
                    shop.shopId = minShop.id
                    list.add(shop)
                }

                callback(list)

            }
    }

    fun getShop(shopId: String, callback: (shop: Shop) -> Unit){

        val db = FirebaseFirestore.getInstance()
        db.collection("shops").document(shopId)
            .get()
            .addOnSuccessListener {

                var shop = Shop()

                if (it != null && it.exists()) {
                    shop = it.toObject(Shop::class.java)!!

                }

                callback(shop)
            }
    }

    fun getUserHistory(callback: (MutableList<History>) -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("customers").document(uid).collection("history").orderBy("dateLong", Query.Direction.DESCENDING)
        val list = mutableListOf<History>()

        docRef.get()
            .addOnSuccessListener {

                for (history in it){
                    println("perLine: " + history)
                    val reminder = history.toObject(History::class.java)
                    list.add(reminder)
                }

                callback(list)

            }
    }

    fun getUserBikeParts(callback: (MutableList<BikeParts>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("customers").document(uid).collection("bike-parts")
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
        val docRef = db.collection("customers").document(uid).collection("refueling")
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
        val docRef = db.collection("customers").document(uid).collection("odometerUpdate")

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


    fun saveRefueling(refueling: Refueling, callback: (String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("customers").document(FirebaseAuth.getInstance().uid!!).collection("refueling")
            .add(refueling)
            .addOnSuccessListener {
                callback(it.id)
            }
            .addOnFailureListener {
                    e -> println(e)
                callback(null)
            }
    }

    fun saveOdometerUpdate(odometerUpdate: OdometerUpdate, callback: (String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        db.collection("customers").document(FirebaseAuth.getInstance().uid!!).collection("odometerUpdate")
            .add(odometerUpdate)
            .addOnSuccessListener {
                callback(it.id)
            }
            .addOnFailureListener {
                    e -> println(e)
                callback(null)
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
        val insuranceDocument = db.collection("customers").document(uid).collection("documents").document("insurance")

        insuranceDocument
            .set(insurance)
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }
    }

    fun saveDriversLicenseDocument(license: DriversLicense, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val licenseDocument = db.collection("customers").document(uid).collection("documents").document("drivers-license")

        licenseDocument
            .set(license)
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }
    }

    fun saveMotorRegistrationDocument(motorRegistration: MotorRegistration, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val document = db.collection("customers").document(uid).collection("documents").document("motor-registration")

        document
            .set(motorRegistration)
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }
    }


    fun getInsuranceDocument(callback: (Insurance?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val insuranceDocument = db.collection("customers").document(uid).collection("documents").document("insurance")

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
        val document = db.collection("customers").document(uid).collection("documents").document("drivers-license")

        document.get()
            .addOnSuccessListener {


                var license: DriversLicense? = null

                if (it != null && it.exists()) {
                    license = it.toObject(DriversLicense::class.java)!!

                }

                callback( license )

            }
    }

    fun getMotorRegistrationDocument(callback: (MotorRegistration?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val document = db.collection("customers").document(uid).collection("documents").document("motor-registration")

        document.get()
            .addOnSuccessListener {


                var motorRegistration: MotorRegistration? = null

                if (it != null && it.exists()) {
                    motorRegistration = it.toObject(MotorRegistration::class.java)!!

                }

                callback( motorRegistration )

            }
    }

    fun updateUserRegistrationProgress(registrationProgress: Int, callback: () -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val document = db.collection("customers").document(uid)

        document
            .update(mapOf("usersRegistrationProgress" to registrationProgress))
            .addOnSuccessListener {
                callback()
            }

    }

    fun uploadImageToFirebaseStorage(imageUri: Uri, callback: (url: String) -> Unit) {

        var filename = UUID.randomUUID().toString()
        var storageRef = FirebaseStorage.getInstance().getReference("/user_uploads/$filename.jpg")

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

    fun getUserBikes(callback: (MutableList<BikeInfo>) -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("bikes").whereEqualTo("userId", uid)

        val list = mutableListOf<BikeInfo>()

        docRef.get()
            .addOnSuccessListener {

                for (bikeDocument in it){
                    val bike = bikeDocument.toObject(BikeInfo::class.java)
                    list.add(bike)
                }

                callback(list)

            }

    }

    fun udpateUserFields(userFields: MutableMap<String, Any>, callback: () -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val ref = db.collection("customers").document(uid)

        ref
            .update(userFields)
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }

    }

    fun getUserType(callback: (String) -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("users").document(uid)

        docRef.get().addOnSuccessListener { documentSnapshot ->

            var user = documentSnapshot.toObject(User::class.java)!!
            callback( user.userType )
        }

    }

    fun addShopToFavorites(shopId: String, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val docRef = db.collection("customers").document(uid)

        docRef.update("favoriteShops", FieldValue.arrayUnion(shopId)).addOnSuccessListener {
            callback()
        }
    }

    fun checkIfFavoriteOrNot(shopId: String, callback: (Boolean) -> Unit) {

        getUser {

            if (it.favoriteShops.contains(shopId)){
                callback(true)
            } else {
                callback(false)
            }

        }



    }

    fun getFavoriteShops(callback: (MutableList<Shop>) -> Unit) {

        getUser {

            val userShopIds = it.favoriteShops

            val db = FirebaseFirestore.getInstance()
            val document = db.collection("shops").whereIn("shopId", userShopIds)
            val list = mutableListOf<Shop>()

            document.get()
                .addOnSuccessListener {

                    for (doc in it){
                        val shop = doc.toObject(Shop::class.java)
                        list.add(shop)
                    }

                    callback(list)

                }

        }
    }

    fun searchShop(searchTag: List<String>, callback: (MutableList<Shop>) -> Unit){

        val db = FirebaseFirestore.getInstance()
        val document = db.collection("shops").whereArrayContainsAny("searchTags", searchTag)
        val list = mutableListOf<Shop>()

        document.get()
            .addOnSuccessListener {

                for (doc in it){
                    val shop = doc.toObject(Shop::class.java)
                    list.add(shop)
                }

                callback(list)

            }

    }

    fun saveDeletedParts(parts: String, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val userBio = db.collection("customers").document(uid)

        userBio
            .update("deletedParts", FieldValue.arrayUnion("$parts "))
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }
    }

    fun saveDeletedBrands(parts: String, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val userBio = db.collection("customers").document(uid)

        userBio
            .update("deletedBrands", FieldValue.arrayUnion("$parts "))
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }
    }

    fun saveDeletedFuels(parts: String, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val userBio = db.collection("customers").document(uid)

        userBio
            .update("deletedFuels", FieldValue.arrayUnion("$parts "))
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e -> callback() }
    }

    fun createNewChatRoom(participants: Map<String, String>, callback: (String) -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val chatRoomRef = db.collection("chat-rooms")

        chatRoomRef
            .add( mapOf("participants" to participants) )
            .addOnSuccessListener {
                println("chat room created!: " + it.id)
                callback( it.id )
            }
            .addOnFailureListener { e ->
                println(e)
                callback( "" )
            }
    }

    fun saveMessageInChatRoom(chatMessage: ChatMessage, callback: () -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val chatRoomRef = db.collection("chat-rooms").document(chatMessage.chatRoomId).collection("chat-messages")

        chatRoomRef
            .add( chatMessage )
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener { e ->
                println(e)
                callback()
            }

    }

    fun updateChatRoomLastMessage(chatRoomId: String, latestMessage: ChatMessage, callback:() -> Unit){

        val db = FirebaseFirestore.getInstance()
        val chatRoomRef = db.collection("chat-rooms").document(chatRoomId)

        chatRoomRef
            .update( mapOf("lastMessage" to latestMessage) )
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener { e ->
                println(e)
                callback()
            }

    }


    fun saveLastMessage(chatMessage: ChatMessage, callback: () -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val lastMessageRef = db.collection("chat-last-messages")

        lastMessageRef
            .add( chatMessage )
            .addOnSuccessListener {
                callback()
            }
            .addOnFailureListener { e ->
                println(e)
                callback()
            }

    }

    fun getChatRoomByParticipants(user: String, shop: String, callback: (String) -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val chatRoomRef = db.collection("chat-rooms")
            .whereEqualTo("participants.user", user )
            .whereEqualTo("participants.shop", shop )

        chatRoomRef.get().addOnSuccessListener {

            if (it.count() != 0){
                println("count of chatroom is exist")
                for (chatRoom in it){
                    callback(chatRoom.id)
                }

            } else {
                println("no chat room found")
                callback("")

            }


        }




    }

    fun updateFcmToken(token: String) {
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val userBio = db.collection("users").document(uid)

        userBio
            .update("token", token)
            .addOnSuccessListener {}
            .addOnFailureListener { e -> println("error update user's fcm token: $e") }
    }

    // on user log-out
    fun deleteUserToken(){
        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val userBio = db.collection("users").document(uid)

        userBio
            .update("token", "")
            .addOnSuccessListener { println("success deleting token")}
            .addOnFailureListener { e -> println("error update user's fcm token: $e") }

    }

    private fun checkIfAchievementExist(achievementName: String, callback: (Achievement?) -> Unit){

        MotoroBroDatabase().getUser{

            val achievements = it.achievements

            if( achievements.containsKey(achievementName) ){
                callback(achievements[achievementName])
            }

            else {
                callback(null)
            }

        }

    }

    fun createAchievement(achievementName: String, callback: (Achievement) -> Unit){

        MotoroBroDatabase().getUser{

            val db = FirebaseFirestore.getInstance()
            val uid = FirebaseAuth.getInstance().uid!!
            val userBio = db.collection("customers").document(uid)
            val newAchievement = Achievement(achievementName)

            userBio
                .update("achievements.$achievementName", newAchievement)
                .addOnSuccessListener { callback( newAchievement ) }
                .addOnFailureListener { e -> println("error creating achievement: $e") }


        }

    }

    fun updateAchievement(achievement: Achievement) {

        val db = FirebaseFirestore.getInstance()
        val uid = FirebaseAuth.getInstance().uid!!
        val userBio = db.collection("customers").document(uid)

        userBio
            .update("achievements.${achievement.name}", achievement)
            .addOnSuccessListener {}
            .addOnFailureListener { e -> println("error updating achievement: $e") }

    }

    fun getAchievement(achievementName: String, callback: (Achievement) -> Unit) {

        checkIfAchievementExist(achievementName){ achievement ->

            if (achievement != null){

                callback(achievement)

            } else {

                createAchievement(achievementName){

                    callback(it)

                }

            }

        }


    }

    fun deleteBike(bike: BikeInfo, callback: () -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val bikeRef = db.collection("bikes").document(bike.bikeId)

        bikeRef.update(mapOf("deleted" to true))
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e->
                println("Error updating bike as deleted: $e")
                callback()
            }

    }

    fun getShopProducts(shopId: String, callback: (MutableList<ShopProduct>) -> Unit) {

        var list = mutableListOf<ShopProduct>()
        val db = FirebaseFirestore.getInstance()
        db.collection("shops")
            .document(shopId)
            .collection("products")
            .get()
            .addOnSuccessListener {

                println()
                for (productDocument in it){
                    val product = productDocument.toObject(ShopProduct::class.java)
                    list.add(product)
                    println("product: " + product.name)
                }

                callback(list)

                println("addOnSuccessListener")
            }
            .addOnFailureListener {
                    e -> println("FailureListener: $e")
                callback(list)
            }

    }
    fun updateBikeById(bikeId: String, bike: BikeInfo, callback: () -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val bikeRef = db.collection("bikes").document(bikeId)

        bikeRef.set(bike)
            .addOnSuccessListener { callback() }
            .addOnFailureListener { e->
                println("Error updating bike: $e")
                callback()
            }

    }

    fun getBikeById(bikeId: String, callback: (BikeInfo) -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("bikes").document(bikeId)

        docRef.get().addOnSuccessListener { documentSnapshot ->

            var bike = BikeInfo()

            if (documentSnapshot != null && documentSnapshot.exists()) {
                bike = documentSnapshot.toObject(BikeInfo::class.java)!!

            }

            callback( bike )
        }

    }


}