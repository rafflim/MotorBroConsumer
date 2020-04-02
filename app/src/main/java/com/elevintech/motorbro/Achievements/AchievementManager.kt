package com.elevintech.motorbro.Achievements

import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase

class AchievementManager {

    fun setAchievementAsAchieved(achievementName: String){

        // get current
        MotoroBroDatabase().getAchievement(achievementName){ achievement ->

            achievement.isAchieved = true

            MotoroBroDatabase().updateAchievement(achievement)


        }

    }

    fun incrementAchievementProgress(achievementName: String, progressValue: Int){

        // get current
        MotoroBroDatabase().getAchievement(achievementName){ achievement ->

            achievement.progress = achievement.progress + progressValue

            // increment value
            MotoroBroDatabase().updateAchievement(achievement)


        }

    }

}