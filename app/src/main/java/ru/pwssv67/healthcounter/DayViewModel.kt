package ru.pwssv67.healthcounter

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.pwssv67.healthcounter.Database.DayStatsDatabase
import ru.pwssv67.healthcounter.Extensions.DayStats
import ru.pwssv67.healthcounter.Extensions.Profile
import java.time.LocalDate

class DayViewModel(application: Application): AndroidViewModel(application) {
    private val dayStatsRepository:DayStatsRepository
    private val preferencesRepository = PreferencesRepository
    private var dayStatsData = MutableLiveData<DayStats>()




    init {
        val dayStatsDao = DayStatsDatabase.getDatabase(App.applicationContext(), viewModelScope).dayStatsDao()
        dayStatsRepository = DayStatsRepository(dayStatsDao)
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
            if (it!=null) {
                Log.e("", it[0].day)
            }

        }
        dayStatsRepository.allDayStats.observeForever(tempObserver)
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

    fun getProfile() = preferencesRepository.getProfileData()

    fun saveProfile(profile: Profile) {
        preferencesRepository.saveProfileData(profile)
    }

    private suspend fun checkIfRecordExists() {
        if (dayStatsRepository.dayStatsData.value == null) {
            dayStatsRepository.saveDayStats(DayStats(0,0,0, LocalDate.now().toString()))
        }
    }
}