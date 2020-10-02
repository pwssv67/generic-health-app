package ru.pwssv67.healthcounter.Extensions

import java.util.*

data class DayStats(var glasses:Int, var calories: Int, var training:Int, val day:Date = Date()) {}