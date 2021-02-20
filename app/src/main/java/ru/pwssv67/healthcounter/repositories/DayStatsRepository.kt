package ru.pwssv67.healthcounter.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import ru.pwssv67.healthcounter.database.DayStatsDao
import ru.pwssv67.healthcounter.extensions.DayStats
import java.time.LocalDate
import java.util.concurrent.Executor

class DayStatsRepository (private val dao:DayStatsDao) {
    val dayStatsData:LiveData<DayStats> = dao.load(LocalDate.now().toString())
    val allDayStats = dao.loadAll()
    private val executor = Executor { Thread(it).start() }

    init{

    }

    suspend fun saveDayStats(dayStats: DayStats) {
       dao.save(dayStats)
        Log.e("rgr", "fefegehewgdsg ${dayStats.day} ${dayStats.glasses}")
    }


}