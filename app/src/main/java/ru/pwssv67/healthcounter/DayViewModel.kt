package ru.pwssv67.healthcounter

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.pwssv67.healthcounter.Database.DayStatsDatabase
import ru.pwssv67.healthcounter.Extensions.DayStats
import java.time.LocalDate

class DayViewModel(application: Application): AndroidViewModel(application) {
    private val repository:DayStatsRepository
    private var dayStatsData = MutableLiveData<DayStats>()


    init {
        val dayStatsDao = DayStatsDatabase.getDatabase(App.applicationContext(), viewModelScope).dayStatsDao()
        repository = DayStatsRepository(dayStatsDao)
        dayStatsData.value = repository.dayStatsData.value
        val dataObserver = Observer<DayStats> {
            if (it != null) {
                dayStatsData.postValue(it)
                Log.e("fef", "${it.glasses}")
            }
        }
        repository.dayStatsData.observeForever(dataObserver)
        viewModelScope.launch {
            delay(1000)
            checkIfRecordExists()
        }
    }

    fun getDayStatsData(): MutableLiveData<DayStats> {
        viewModelScope.launch {
            dayStatsData.postValue(repository.dayStatsData.value)
        }

        return dayStatsData
    }

    fun saveDayStatsData(dayStats: DayStats) {
        viewModelScope.launch {
            repository.saveDayStats(dayStats)
        }
        //dayStatsData.value = dayStats
    }

    private suspend fun checkIfRecordExists() {
        if (repository.dayStatsData.value == null) {
            repository.saveDayStats(DayStats(0,0,0, LocalDate.now().toString()))
        }
    }
}