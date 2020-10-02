package ru.pwssv67.healthcounter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.pwssv67.healthcounter.Extensions.DayStats

class DayViewModel:ViewModel() {
    private val repository = Repository
    private val dayStatsData = MutableLiveData<DayStats>()

    init {
        dayStatsData.value = repository.getDayStats()
    }

    fun getDayStatsData(): MutableLiveData<DayStats> = dayStatsData

    fun saveDayStatsData(dayStats: DayStats) {
        repository.saveDayStats(dayStats)
        dayStatsData.value = dayStats
    }
}