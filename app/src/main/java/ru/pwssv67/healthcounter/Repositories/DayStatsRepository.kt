package ru.pwssv67.healthcounter.Repositories

import android.util.Log
import androidx.lifecycle.LiveData
import ru.pwssv67.healthcounter.Database.DayStatsDao
import ru.pwssv67.healthcounter.Extensions.DayStats
import java.time.LocalDate
import java.util.concurrent.Executor

class DayStatsRepository (val dao:DayStatsDao) {
    val dayStatsData:LiveData<DayStats> = dao.load(LocalDate.now().toString())
    val allDayStats = dao.loadAll()
    private val executor = Executor { Thread(it).start() }

    init{

    }
    suspend fun saveDayStats(dayStats: DayStats) {
       dao.save(dayStats)
        Log.e("rgr", "fefegehewgdsg ${dayStats.day} ${dayStats.glasses}")
    }

    fun loadAll():LiveData<List<DayStats>> {
        val data = dao.loadAll()
        Log.e("rgrgrggr", "fff")
        return data
    }

    /*

    suspend fun getDayStats(day: String = LocalDate.now().toString()): DayStats? {
        val data =dao.load()
        Log.e("rgr", "get ${data}")
        if (data.isNullOrEmpty()) {
            val temp = DayStats(0,0,0)
            saveDayStats(temp)
            return temp
        }
        Log.e("rgr", "get ${data[0].day} ${data[0].glasses}")
        return data[0]
    }

     */

}