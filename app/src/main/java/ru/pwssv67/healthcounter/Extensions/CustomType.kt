package ru.pwssv67.healthcounter.Extensions

import android.graphics.Paint
import android.graphics.RectF
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DecimalFormat
import java.time.LocalDate

@Entity
data class DayStats(var glasses:Int, var calories: Int, var training:Int, @PrimaryKey val day:String = LocalDate.now().toString()) {}

enum class Goal(val goalType: String){
    GLASSES("GLASSES"),
    CALORIES("CALORIES"),
    TRAINING("TRAINING")
}

class Profile(var name:String, var drink_goal:Int, var training_goal:Int, var eat_goal_first:Int, var eat_goal_second:Int)


class ChartBar(rect:RectF, paint: Paint) {
    var rect: RectF
    var value:Int = 0
    var paint: Paint
    var color:Int = 0
    var i:Int = -1

    init {
        this.rect = rect
        this.paint = paint
    }
}
