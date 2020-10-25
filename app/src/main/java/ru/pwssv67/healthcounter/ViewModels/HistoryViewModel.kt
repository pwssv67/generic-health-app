package ru.pwssv67.healthcounter.ViewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import ru.pwssv67.healthcounter.App
import ru.pwssv67.healthcounter.Database.DayStatsDao
import ru.pwssv67.healthcounter.Database.DayStatsDatabase
import ru.pwssv67.healthcounter.Repositories.DayStatsRepository
import ru.pwssv67.healthcounter.Extensions.DayStats
import ru.pwssv67.healthcounter.Extensions.Profile
import ru.pwssv67.healthcounter.Repositories.PreferencesRepository

class HistoryViewModel(application: Application):AndroidViewModel(application) {

    val dao:DayStatsDao
    val repository: DayStatsRepository?
    val days = MutableLiveData<List<DayStats>>()
    val preferencesRepository =
        PreferencesRepository

    init {
        dao = DayStatsDatabase.getDatabase(App.applicationContext(), viewModelScope).dayStatsDao()
        repository = DayStatsDatabase.Repository
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