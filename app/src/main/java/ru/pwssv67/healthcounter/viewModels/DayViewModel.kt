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
import ru.pwssv67.healthcounter.extensions.getLocation
import ru.pwssv67.healthcounter.models.WeatherForecastModel
import ru.pwssv67.healthcounter.network.NetworkService
import ru.pwssv67.healthcounter.notification.NotificationHandler
import ru.pwssv67.healthcounter.repositories.PreferencesRepository
import ru.pwssv67.healthcounter.ui.activities.MainActivity
import java.time.LocalDate
import java.util.*

class DayViewModel(application: Application): AndroidViewModel(application) {
    private val dayStatsRepository: DayStatsRepository
    private val preferencesRepository =
        PreferencesRepository
    private var dayStatsData = MutableLiveData<DayStats>()
    var weatherData = MutableLiveData<WeatherForecastModel>()
    private lateinit var locationManager : LocationManager
    val locationPermissionData = MutableLiveData<Boolean>()
    var weather: WeatherForecastModel? = null
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

        locationPermissionData.postValue(false)

        locationPermissionData.observeForever {
            if (it) viewModelScope.launch {
                while (!checkLocation()){
                    delay(100000)
                }
            }
        }

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

    private fun checkLocation():Boolean {
        val location = getLocation(getApplication())
        if (location != null) {
            preferencesRepository.saveLatLongFromLocation(location)
            return true
        } else {
            return false
        }
    }

    private suspend fun checkIfRecordExists() {
        if (dayStatsRepository.dayStatsData.value == null) {
            dayStatsRepository.saveDayStats(DayStats(0,0,0, LocalDate.now().toString()))
        }
    }



}