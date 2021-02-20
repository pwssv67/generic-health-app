package ru.pwssv67.healthcounter

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import androidx.work.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.pwssv67.healthcounter.workers.WeatherForecastWorker
import java.util.concurrent.TimeUnit

class App:Application() {



    companion object {
            private var instance:App? = null
            val NOTIFICATION_CHANNEL_1_ID = "420"
            val WEATHER_WORKER_NAME = "WEATHER_WORKER"
            fun applicationContext(): Context {
                return instance!!.applicationContext
            }

    }

    init {
        instance = this
        MainScope().launch { launchBackgroundWorker() }
    }

    private suspend fun launchBackgroundWorker() {
        delay(1000)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val myWorkRequest = PeriodicWorkRequestBuilder<WeatherForecastWorker>(
            1, TimeUnit.HOURS
        )
            //.setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.MINUTES)
            .build()


        WorkManager
            .getInstance(applicationContext)
            //.enqueue(testWorkRequest)
            .enqueueUniquePeriodicWork(WEATHER_WORKER_NAME,ExistingPeriodicWorkPolicy.REPLACE, myWorkRequest )
    }


}