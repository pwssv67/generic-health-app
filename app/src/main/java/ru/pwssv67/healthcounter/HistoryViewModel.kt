package ru.pwssv67.healthcounter

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import ru.pwssv67.healthcounter.Database.DayStatsDao
import ru.pwssv67.healthcounter.Database.DayStatsDatabase
import ru.pwssv67.healthcounter.Extensions.DayStats

class HistoryViewModel(application: Application):AndroidViewModel(application) {

    val dao:DayStatsDao
    val repository:DayStatsRepository?
    val days = MutableLiveData<List<DayStats>>()

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
}