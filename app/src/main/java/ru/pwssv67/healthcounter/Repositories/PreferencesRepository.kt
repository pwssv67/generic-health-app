package ru.pwssv67.healthcounter.Repositories

import android.content.SharedPreferences
import android.preference.PreferenceManager
import ru.pwssv67.healthcounter.App
import ru.pwssv67.healthcounter.Extensions.Profile

object PreferencesRepository{

    private const val DRINK_GOAL = "DRINK_GOAL"
    private const val TRAINING_GOAL = "TRAINING_GOAL"
    private const val EAT_GOAL_FIRST = "EAT_GOAL_FIRST"
    private const val EAT_GOAL_SECOND = "EAT_GOAL_SECOND"
    private const val NAME = "NAME"

    private val prefs: SharedPreferences by lazy {
        val ctx= App.applicationContext()
        PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    fun getProfileData(): Profile = Profile (
        prefs.getString(
            NAME, "")?:"",
        prefs.getInt(
            DRINK_GOAL, 8),
        prefs.getInt(
            TRAINING_GOAL, 30),
        prefs.getInt(
            EAT_GOAL_FIRST, 1500),
        prefs.getInt(
            EAT_GOAL_SECOND, 2500)
    )

    fun saveProfileData(profile: Profile) {
        with(profile) {
            putValue(
                NAME to name
            )
            putValue(
                DRINK_GOAL to drink_goal
            )
            putValue(
                TRAINING_GOAL to training_goal
            )
            putValue(
                EAT_GOAL_FIRST to eat_goal_first
            )
            putValue(
                EAT_GOAL_SECOND to eat_goal_second
            )
        }
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