package ru.pwssv67.healthcounter

import android.app.Application
import android.app.NotificationManager
import android.content.Context

class App:Application() {



    companion object {
            private var instance:App? = null
            val NOTIFICATION_CHANNEL_1_ID = "420"
            fun applicationContext(): Context {
                return instance!!.applicationContext
            }

    }

    init {
        instance = this
    }



}