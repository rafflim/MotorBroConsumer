package com.elevintech.motorbro.CloudFunctions

import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.iid.FirebaseInstanceId

class CloudFunctions {

    val cloudFunction = FirebaseFunctions.getInstance()

    // TODO: Change this for firestore
    fun retrieveCurrentRegistrationToken(callback: (String) -> Unit){
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    println("getInstanceId failed" + task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result?.token

                // Log and toast
                println("FCM token: " + token)

                callback(token!!)
            })
    }

    fun checkRegistrationToken(){

        retrieveCurrentRegistrationToken{ currentToken ->

            MotoroBroDatabase().getUserToken { savedToken ->

                if (savedToken != currentToken){
                    MotoroBroDatabase().updateFcmToken(currentToken)
                }

            }

        }
    }

}