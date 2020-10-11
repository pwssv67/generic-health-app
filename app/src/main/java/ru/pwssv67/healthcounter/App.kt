package ru.pwssv67.healthcounter

import android.app.Application
import android.content.Context
import androidx.room.Room
import ru.pwssv67.healthcounter.Database.DayStatsDatabase

class App:Application() {


    companion object {
            private var instance:App? = null


            fun applicationContext(): Context {
                return instance!!.applicationContext
            }

    }

    init {
        instance = this

    }



}