package ru.pwssv67.healthcounter.Database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import ru.pwssv67.healthcounter.Extensions.DayStats

@Dao
interface DayStatsDao {
    @Insert (onConflict = REPLACE)
    suspend fun save(dayStats: DayStats)

    @Query ("SELECT * FROM daystats WHERE day=:day")
    fun load(day:String): LiveData<DayStats>

    @Query("DELETE FROM daystats")
    suspend fun deleteAll()
}