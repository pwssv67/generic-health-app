package ru.pwssv67.healthcounter.Extensions

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.*

@Entity
data class DayStats(var glasses:Int, var calories: Int, var training:Int, @PrimaryKey val day:String = LocalDate.now().toString()) {}

enum class Goal(val goalType: String){
    GLASSES("GLASSES"),
    CALORIES("CALORIES"),
    TRAINING("TRAINING")
}