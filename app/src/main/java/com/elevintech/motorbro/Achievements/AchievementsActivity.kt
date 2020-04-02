package com.elevintech.motorbro.Achievements

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.elevintech.motorbro.Model.Achievement
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.myapplication.R
import kotlinx.android.synthetic.main.activity_achievements.*

class AchievementsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)

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

            }

            if (firstOdometerUpdate != null && firstOdometerUpdate.isAchieved){

                odometer_reader_progress_bar.max = 1
                odometer_reader_progress_bar.progress = 1

            }

            if (firstPartService != null && firstPartService.isAchieved){

                entry_mechanic_progress_bar.max = 1
                entry_mechanic_progress_bar.progress = 1

            }

            if (firstFuel != null && firstFuel.isAchieved){

                first_fuel_progress_bar.max = 1
                first_fuel_progress_bar.progress = 1

            }

            if (distanceTraveled != null){

                traveler_progress_bar.max = 20000
                traveler_progress_bar.progress = distanceTraveled.progress

            }

            if (refuelTimes != null){

                refuel_master_progress_bar.max = 500
                refuel_master_progress_bar.progress = refuelTimes.progress

            }

            if (createdPartService != null){

                service_master_progress_bar.max = 100
                service_master_progress_bar.progress = createdPartService.progress

            }



        }

        backView.setOnClickListener {
            finish()
        }
    }
}
