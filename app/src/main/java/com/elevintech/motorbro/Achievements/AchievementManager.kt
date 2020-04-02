package com.elevintech.motorbro.Achievements

import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase

class AchievementManager {

    fun setAchievementAsAchieved(achievementName: String){

        MotoroBroDatabase().setAchievementAsAchieved(achievementName)

    }

}