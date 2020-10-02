package ru.pwssv67.healthcounter

import android.content.SharedPreferences
import android.preference.PreferenceManager
import ru.pwssv67.healthcounter.Extensions.DayStats

object Repository {
    private const val GLASSES = "GLASSES"
    private const val CALORIES = "CALORIES"
    private const val TRAINING = "TRAINING"
    private const val DATE = "DATE"


    private val prefs: SharedPreferences by lazy {
        val ctx = App.applicationContext()
        PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun saveDayStats(dayStats: DayStats) {
        with (dayStats) {
            putValue(GLASSES to glasses)
            putValue(CALORIES to calories)
            putValue(TRAINING to training)
        }
    }

    fun getDayStats(): DayStats {
        return DayStats(
            prefs.getInt(GLASSES, 0),
            prefs.getInt(CALORIES, 0),
            prefs.getInt(TRAINING, 0)
        )
    }

    private fun putValue(pair: Pair<String,Any>) = with(prefs.edit()) {
        val key = pair.first
        val value = pair.second

        when (value) {
            is String -> putString(key,value)
            is Int -> putInt(key,value)
            is Boolean -> putBoolean(key,value)
            is Long -> putLong (key,value)
            is Float -> putFloat (key,value)
            else -> error("Only primitive data types can be stored in Shared Preferences")
        }
        apply()
    }
}