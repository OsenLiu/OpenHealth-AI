package com.osen.sanoai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.osen.sanoai.data.local.dao.HealthDao
import com.osen.sanoai.data.local.entities.DailySuggestion
import com.osen.sanoai.data.local.entities.ExerciseLog
import com.osen.sanoai.data.local.entities.FoodLog
import com.osen.sanoai.data.local.entities.UserProfile
import com.osen.sanoai.data.local.entities.WeightRecord

@Database(
    entities = [
        UserProfile::class,
        WeightRecord::class,
        FoodLog::class,
        ExerciseLog::class,
        DailySuggestion::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun healthDao(): HealthDao

    companion object {
        const val DATABASE_NAME = "sanoai_db"
    }
}
