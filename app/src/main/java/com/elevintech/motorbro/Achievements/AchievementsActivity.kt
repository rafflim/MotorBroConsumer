package com.elevintech.motorbro.Achievements

import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.elevintech.motorbro.Model.Achievement
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils
import com.elevintech.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_achievements.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

class AchievementsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)
        new_start_progress_bar.scaleY = 3f
        MotoroBroDatabase().getUser{

            val achievements = it.achievements

            val newStart = achievements[Achievement.Names.FIRST_MONITOR]
            val firstOdometerUpdate = achievements[Achievement.Names.FIRST_ODOMETER]
            val firstPartService = achievements[Achievement.Names.FIRST_PART_SERVICE]
            val firstFuel = achievements[Achievement.Names.FIRST_FUEL]

            val distanceTraveled = achievements[Achievement.Names.DISTANCE_TRAVELED]
            val refuelTimes = achievements[Achievement.Names.REFUEL_TIMES]
            val createdPartService = achievements[Achievement.Names.CREATED_PART_SERVICE]


            if (newStart != null && newStart.isAchieved){

                new_start_progress_bar.max = 1
                new_start_progress_bar.progress = 1

                applyAchievedStyling(new_start_text)

            }

            if (firstOdometerUpdate != null && firstOdometerUpdate.isAchieved){

                odometer_reader_progress_bar.max = 1
                odometer_reader_progress_bar.progress = 1

                applyAchievedStyling(odometer_reader_text)
            }

            if (firstPartService != null && firstPartService.isAchieved){

                entry_mechanic_progress_bar.max = 1
                entry_mechanic_progress_bar.progress = 1

                applyAchievedStyling(entry_mechanic_text)

            }

            if (firstFuel != null && firstFuel.isAchieved){

                first_fuel_progress_bar.max = 1
                first_fuel_progress_bar.progress = 1

                applyAchievedStyling(first_fuel_text)
            }

            if (distanceTraveled != null){

                traveler_progress_bar.max = 20000
                traveler_progress_bar.progress = distanceTraveled.progress

                if(distanceTraveled.progress >= 1000) applyAchievedStyling( traveler_progress_1 )
                if(distanceTraveled.progress >= 5000) applyAchievedStyling( traveler_progress_2 )
                if(distanceTraveled.progress >= 10000) applyAchievedStyling( traveler_progress_3 )
                if(distanceTraveled.progress >= 20000) applyAchievedStyling( traveler_progress_4 )


            }

            if (refuelTimes != null){

                refuel_master_progress_bar.max = 500
                refuel_master_progress_bar.progress = refuelTimes.progress

                if(refuelTimes.progress >= 50) applyAchievedStyling( refuel_master_progress_1 )
                if(refuelTimes.progress >= 75) applyAchievedStyling( refuel_master_progress_2 )
                if(refuelTimes.progress >= 100) applyAchievedStyling( refuel_master_progress_3 )
                if(refuelTimes.progress >= 250) applyAchievedStyling( refuel_master_progress_4 )
                if(refuelTimes.progress >= 500) applyAchievedStyling( refuel_master_progress_5 )

            }

            if (createdPartService != null){

                service_master_progress_bar.max = 100
                service_master_progress_bar.progress = createdPartService.progress


                if(createdPartService.progress >= 10) applyAchievedStyling( service_master_progress_1 )
                if(createdPartService.progress >= 25) applyAchievedStyling( service_master_progress_2 )
                if(createdPartService.progress >= 50) applyAchievedStyling( service_master_progress_3 )
                if(createdPartService.progress >= 100) applyAchievedStyling( service_master_progress_4 )
            }

            val userCreatedDate = FirebaseAuth.getInstance().currentUser!!.metadata!!.creationTimestamp
            val userDurationInMonths = Utils().getMonthsFromNow(userCreatedDate)
            if (userDurationInMonths >= 1){

                loyal_motorbro_progress_bar.max = 24 // months = 2 years max
                loyal_motorbro_progress_bar.progress = userDurationInMonths

                if(userDurationInMonths >= 1) applyAchievedStyling( loyal_motorbro_progress_1 )
                if(userDurationInMonths >= 6) applyAchievedStyling( loyal_motorbro_progress_2 )
                if(userDurationInMonths >= 12) applyAchievedStyling( loyal_motorbro_progress_3 )
                if(userDurationInMonths >= 24) applyAchievedStyling( loyal_motorbro_progress_4 )

            }


        }

        backView.setOnClickListener {
            finish()
        }
    }

    fun applyAchievedStyling(textView: TextView){

        textView.textSize = 16f
        textView.typeface = Typeface.DEFAULT_BOLD
        textView.setTextColor(resources.getColor(R.color.achievedGreen))

    }
}
