package ru.pwssv67.healthcounter.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.pwssv67.healthcounter.DayStatsRepository
import ru.pwssv67.healthcounter.Extensions.DayStats

@Database(entities = [DayStats::class], version = 1)
abstract class DayStatsDatabase : RoomDatabase() {
    abstract fun dayStatsDao(): DayStatsDao

    private class DayStatsDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val dayStatsDao = database.dayStatsDao()
                    dayStatsDao.delete("1997-12-12")
                    dayStatsDao.delete("11-10-2020")
                }
            }
        }
    }



    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: DayStatsDatabase? = null
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `dayStats_database` (`glasses` INTEGER,`calories` INTEGER, `training` INTEGER, `day` TEXT, " +
                        "PRIMARY KEY(`day`))")
            }
        }

        public var Repository:DayStatsRepository? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): DayStatsDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DayStatsDatabase::class.java,
                    "dayStats_database"
                )
                    .addCallback(DayStatsDatabaseCallback(scope))
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}