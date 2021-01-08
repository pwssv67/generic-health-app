package ru.pwssv67.healthcounter.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import ru.pwssv67.healthcounter.App
import ru.pwssv67.healthcounter.database.DayStatsDao
import ru.pwssv67.healthcounter.database.DayStatsDatabase
import ru.pwssv67.healthcounter.repositories.DayStatsRepository
import ru.pwssv67.healthcounter.extensions.DayStats
import ru.pwssv67.healthcounter.extensions.Profile
import ru.pwssv67.healthcounter.repositories.PreferencesRepository

class HistoryViewModel(application: Application):AndroidViewModel(application) {

    val dao:DayStatsDao = DayStatsDatabase.getDatabase(App.applicationContext(), viewModelScope).dayStatsDao()
    private val repository: DayStatsRepository? = DayStatsDatabase.Repository
    private val days = MutableLiveData<List<DayStats>>()
    val preferencesRepository =
        PreferencesRepository

    init {
        val observer = Observer<List<DayStats>> {
            if (it != null) {
                days.postValue(it)
                Log.e("rgr", it[0].day)
            }
        }
        repository?.allDayStats?.observeForever(observer)
    }

    fun getDayStats():MutableLiveData<List<DayStats>> {
        return days
    }
    fun getProfile():Profile =
        PreferencesRepository.getProfileData()
}