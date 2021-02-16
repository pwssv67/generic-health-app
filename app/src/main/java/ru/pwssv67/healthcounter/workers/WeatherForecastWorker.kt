package ru.pwssv67.healthcounter.workers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.pwssv67.healthcounter.App
import ru.pwssv67.healthcounter.R
import ru.pwssv67.healthcounter.extensions.getLocation
import ru.pwssv67.healthcounter.models.WeatherForecastModel
import ru.pwssv67.healthcounter.network.NetworkService
import ru.pwssv67.healthcounter.notification.NotificationHandler

class WeatherForecastWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams){
    override fun doWork(): Result {
        val weather = getWeatherForecast()
        if (weather != null) {
            val builder = NotificationCompat.Builder(applicationContext, App.NOTIFICATION_CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_flame)
                .setContentTitle("Weather is Grrreat!")
                .setContentText("Worker's temperature is ${weather.current.temp_c}° C")
            NotificationHandler.showNotification(builder.build(), 1)
        }
        return Result.success(Data.Builder().putString("weather", Json.encodeToString(weather)).build())
    }

    private fun getWeatherForecast(): WeatherForecastModel? {
        if (hasPermissions()) {
            val location = getLocation(applicationContext)
            val response = NetworkService.weatherApi.getData(
                NetworkService.key,
                location?.latitude.toString() + ',' + location?.longitude.toString()
            )
                .execute()
            return response.body()
                    /*
                .enqueue(
                    object : Callback<WeatherForecastModel> {
                        override fun onResponse (
                            call: Call<WeatherForecastModel?>,
                            response: Response<WeatherForecastModel?>
                        ) {
                            if (response.body() != null) {
                                weather = response.body()
                            }

                        }

                        override fun onFailure(call: Call<WeatherForecastModel?>, t: Throwable) {
                            t.printStackTrace()
                        }
                    }
                )
                     */

        } else {
            return null
        }
    }

    private fun hasPermissions(): Boolean {
         return applicationContext.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                 applicationContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                 PackageManager.PERMISSION_GRANTED
    }


}