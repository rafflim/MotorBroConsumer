package com.elevintech.motorbro.SplashPage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.elevintech.motorbro.Dashboard.DashboardActivity
import com.elevintech.myapplication.R

class SplashPageActivity : AppCompatActivity() {

    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 3000 //3 seconds

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

//            val sharedPref = SharedPreferences(applicationContext)
//            //val isNotFirstTimeOpening = sharedPref.getValueBool("IS_NOT_FIRST_TIME_OPENING_APP"
//            val isNotFirstTimeOpening = false

            val intent = Intent(applicationContext, DashboardActivity::class.java)
            startActivity(intent)
            finish()


        }
    }
}
