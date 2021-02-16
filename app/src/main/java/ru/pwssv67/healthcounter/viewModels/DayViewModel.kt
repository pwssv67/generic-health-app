package ru.pwssv67.healthcounter.viewModels

import android.Manifest
import android.app.*
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.pwssv67.healthcounter.App
import ru.pwssv67.healthcounter.R
import ru.pwssv67.healthcounter.database.DayStatsDatabase
import ru.pwssv67.healthcounter.repositories.DayStatsRepository
import ru.pwssv67.healthcounter.extensions.DayStats
import ru.pwssv67.healthcounter.extensions.Profile
import ru.pwssv67.healthcounter.models.WeatherForecastModel
import ru.pwssv67.healthcounter.network.NetworkService
import ru.pwssv67.healthcounter.notification.NotificationHandler
import ru.pwssv67.healthcounter.repositories.PreferencesRepository
import ru.pwssv67.healthcounter.ui.activities.MainActivity
import java.time.LocalDate
import java.util.*

class DayViewModel(application: Application, val currActivity: Activity): AndroidViewModel(application) {
    private val dayStatsRepository: DayStatsRepository
    private val preferencesRepository =
        PreferencesRepository
    private var dayStatsData = MutableLiveData<DayStats>()
    var weatherData = MutableLiveData<WeatherForecastModel>()
    private lateinit var locationManager : LocationManager
    var weather: WeatherForecastModel? = null
    val applicationContext = App.applicationContext()
    val NOTIFICATION_ID = 1
    var mTimer: Timer? = null
    var mTimerTask: TimerTask? = null



    init {
        val dayStatsDao = DayStatsDatabase.getDatabase(App.applicationContext(), viewModelScope).dayStatsDao()
        dayStatsRepository =
            DayStatsRepository(dayStatsDao)
        DayStatsDatabase.Repository = dayStatsRepository
        dayStatsData.value = dayStatsRepository.dayStatsData.value
        val dataObserver = Observer<DayStats> {
            if (it != null) {
                dayStatsData.postValue(it)
                Log.e("fef", "${it.glasses}")
            }
        }
        dayStatsRepository.dayStatsData.observeForever(dataObserver)
        viewModelScope.launch {
            delay(1000)
            checkIfRecordExists()
        }

        val tempObserver = Observer<List<DayStats>> {
            if (it!=null && !it.isNullOrEmpty()) {
                Log.e("", it[0].day)
            }

        }
        dayStatsRepository.allDayStats.observeForever(tempObserver)
        locationManager = application.getSystemService(LOCATION_SERVICE) as LocationManager
        //viewModelScope.launch { getWeatherForecast() }
        createTimer()

        weatherData.observeForever(Observer {
            //createWeatherNotification(it)
        })


    }

    fun getDayStatsData(): MutableLiveData<DayStats> {
        viewModelScope.launch {
            dayStatsData.postValue(dayStatsRepository.dayStatsData.value)
        }

        return dayStatsData
    }

    fun saveDayStatsData(dayStats: DayStats) {
        viewModelScope.launch {
            dayStatsRepository.saveDayStats(dayStats)
        }
        //dayStatsData.value = dayStats
    }

    fun getProfile() =
        PreferencesRepository.getProfileData()

    fun saveProfile(profile: Profile) {
        PreferencesRepository.saveProfileData(profile)
    }

    private suspend fun checkIfRecordExists() {
        if (dayStatsRepository.dayStatsData.value == null) {
            dayStatsRepository.saveDayStats(DayStats(0,0,0, LocalDate.now().toString()))
        }
    }

    private fun getWeatherForecast() {

        if (currActivity !is MainActivity) return
        try {
            val location = if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) { ActivityCompat.requestPermissions(currActivity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 101)
                return
            } else {
                locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
            }

            //Log.e("location", "${location.latitude}")

            val body = NetworkService.weatherApi.getData(
                NetworkService.key,
                location.latitude.toString() + ',' + location.longitude.toString()
            )
                .enqueue(
                    object : Callback<WeatherForecastModel> {
                        override fun onResponse(
                            call: Call<WeatherForecastModel?>,
                            response: Response<WeatherForecastModel?>
                        ) {
                            if (response.body() != null) {
                                weatherData.postValue(response.body())
                            }

                            }

                        override fun onFailure(call: Call<WeatherForecastModel?>, t: Throwable) {
                            t.printStackTrace()
                        }
                    }
                )

        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }
    }

    private fun createWeatherNotification(weatherForecastModel: WeatherForecastModel) {
        val builder = NotificationCompat.Builder(applicationContext, App.NOTIFICATION_CHANNEL_1_ID)
            .setSmallIcon(R.drawable.ic_flame)
            .setContentTitle("Weather is Grrreat!")
            .setContentText("Temperature is ${weatherForecastModel.current.temp_c}° C")
            NotificationHandler.showNotification(builder.build(), NOTIFICATION_ID)
    }

    private fun createTimer(){
        if (mTimer != null) mTimer?.cancel()
        mTimer = Timer()
        mTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                getWeatherForecast()
            }
        },
        0,
        1*30*1000)
    }


}