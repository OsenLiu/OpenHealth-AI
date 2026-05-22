package com.osen.sanoai.data.local.dao

import androidx.room.*
import com.osen.sanoai.data.local.entities.DailySuggestion
import com.osen.sanoai.data.local.entities.ExerciseLog
import com.osen.sanoai.data.local.entities.FoodLog
import com.osen.sanoai.data.local.entities.UserProfile
import com.osen.sanoai.data.local.entities.WeightRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthDao {

    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    // Weight Records
    @Query("SELECT * FROM weight_records ORDER BY timestamp DESC")
    fun getAllWeightRecords(): Flow<List<WeightRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightRecord(record: WeightRecord)

    @Delete
    suspend fun deleteWeightRecord(record: WeightRecord)

    // Food Logs
    @Query("SELECT * FROM food_logs ORDER BY timestamp DESC")
    fun getAllFoodLogs(): Flow<List<FoodLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodLog(log: FoodLog)

    @Delete
    suspend fun deleteFoodLog(log: FoodLog)

    // Exercise Logs
    @Query("SELECT * FROM exercise_logs ORDER BY timestamp DESC")
    fun getAllExerciseLogs(): Flow<List<ExerciseLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseLog(log: ExerciseLog)

    @Delete
    suspend fun deleteExerciseLog(log: ExerciseLog)

    // Daily Suggestions
    @Query("SELECT * FROM daily_suggestions WHERE date = :date")
    suspend fun getSuggestionByDate(date: String): DailySuggestion?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuggestion(suggestion: DailySuggestion)
}
