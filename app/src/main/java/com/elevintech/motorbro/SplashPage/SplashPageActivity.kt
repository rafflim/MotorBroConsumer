package com.elevintech.motorbro.SplashPage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.elevintech.motorbro.BikeRegistration.BikeRegistrationActivity
import com.elevintech.motorbro.Dashboard.DashboardActivity
import com.elevintech.motorbro.MainActivity
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


class SplashPageActivity : AppCompatActivity() {

    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds
    private lateinit var auth: FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_page)

        //Initialize the Handler
        mDelayHandler = Handler()

        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)
    }

    public override fun onDestroy() {

        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }

        super.onDestroy()
    }

    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {


            // Initialize Firebase Auth
            auth = FirebaseAuth.getInstance()

            val currentUser = auth.currentUser
//            if (currentUser != null) {
//
//                val firebaseDb = MotoroBroDatabase()
//                firebaseDb.getUser {
//
//                    if (it.usersRegistrationProgress == 1) {
//                        val intent = Intent(applicationContext, BikeRegistrationActivity::class.java)
//                        intent.putExtra("previousActivity", "splashPage")
//                        startActivity(intent)
//                        finish()
//                    } else {
//                        val intent = Intent(applicationContext, DashboardActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//                }
//
//            } else {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
//            }

//            val sharedPref = SharedPreferences(applicationContext)
//            //val isNotFirstTimeOpening = sharedPref.getValueBool("IS_NOT_FIRST_TIME_OPENING_APP"
//            val isNotFirstTimeOpening = false




        }
    }
}
