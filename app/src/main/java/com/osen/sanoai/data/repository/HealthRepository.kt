package com.osen.sanoai.data.repository

import android.graphics.Bitmap
import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.data.api.AiRepository
import com.osen.sanoai.data.api.model.ChatMessage
import com.osen.sanoai.data.api.model.HealthSuggestionResponse
import com.osen.sanoai.data.local.dao.HealthDao
import com.osen.sanoai.data.local.entities.DailySuggestion
import com.osen.sanoai.data.local.entities.ExerciseLog
import com.osen.sanoai.data.local.entities.FoodLog
import com.osen.sanoai.data.local.entities.UserProfile
import com.osen.sanoai.data.local.entities.WeightRecord
import com.osen.sanoai.data.secure.SecureStorage
import kotlinx.coroutines.flow.Flow

class HealthRepository(
    private val healthDao: HealthDao,
    private val secureStorage: SecureStorage,
    private val aiRepository: AiRepository
) {
    // User Profile
    fun getUserProfile(): Flow<UserProfile?> = healthDao.getUserProfile()
    suspend fun saveUserProfile(profile: UserProfile) = healthDao.insertUserProfile(profile)

    // Weight Records
    fun getAllWeightRecords(): Flow<List<WeightRecord>> = healthDao.getAllWeightRecords()
    suspend fun addWeightRecord(record: WeightRecord) = healthDao.insertWeightRecord(record)
    suspend fun deleteWeightRecord(record: WeightRecord) = healthDao.deleteWeightRecord(record)

    // Food Logs
    fun getAllFoodLogs(): Flow<List<FoodLog>> = healthDao.getAllFoodLogs()
    fun getFoodLogsInRange(startTime: Long, endTime: Long): Flow<List<FoodLog>> = healthDao.getFoodLogsInRange(startTime, endTime)
    suspend fun addFoodLog(log: FoodLog) = healthDao.insertFoodLog(log)
    suspend fun deleteFoodLog(log: FoodLog) = healthDao.deleteFoodLog(log)

    // Exercise Logs
    fun getAllExerciseLogs(): Flow<List<ExerciseLog>> = healthDao.getAllExerciseLogs()
    fun getExerciseLogsInRange(startTime: Long, endTime: Long): Flow<List<ExerciseLog>> = healthDao.getExerciseLogsInRange(startTime, endTime)
    suspend fun addExerciseLog(log: ExerciseLog) = healthDao.insertExerciseLog(log)
    suspend fun updateExerciseLog(log: ExerciseLog) = healthDao.updateExerciseLog(log)
    suspend fun deleteExerciseLog(log: ExerciseLog) = healthDao.deleteExerciseLog(log)

    // AI Operations
    suspend fun analyzeFood(bitmap: Bitmap, provider: AiProvider, modelName: String) = aiRepository.analyzeFood(bitmap, provider, modelName)
    suspend fun analyzeFoodText(description: String, provider: AiProvider, modelName: String) = aiRepository.analyzeFoodText(description, provider, modelName)
    suspend fun analyzeExercise(description: String, provider: AiProvider, modelName: String) = aiRepository.analyzeExercise(description, provider, modelName)
    suspend fun generateHealthSuggestion(profile: String, logs: String, provider: AiProvider, modelName: String) = 
        aiRepository.generateHealthSuggestion(profile, logs, provider, modelName)

    suspend fun chatWithConsultant(message: String, profile: String, logs: String, history: List<ChatMessage>, provider: AiProvider, modelName: String) =
        aiRepository.chatWithConsultant(message, profile, logs, history, provider, modelName)

    fun parseSuggestionJson(json: String) = aiRepository.parseSuggestionJson(json)
    fun toJson(suggestion: HealthSuggestionResponse) = aiRepository.suggestionToJson(suggestion)

    // API Keys
    fun saveApiKey(provider: String, apiKey: String) = secureStorage.saveApiKey(provider, apiKey)
    fun getApiKey(provider: String): String? = secureStorage.getApiKey(provider)

    fun saveSelectedAiProvider(provider: AiProvider) = secureStorage.saveSelectedProvider(provider.name)
    fun getSelectedAiProvider(): AiProvider {
        val name = secureStorage.getSelectedProvider()
        return if (name != null) AiProvider.valueOf(name) else AiProvider.GEMINI
    }

    fun saveModelName(provider: AiProvider, modelName: String) =
        secureStorage.saveModelName(provider.name, modelName)

    fun getModelName(provider: AiProvider): String {
        return secureStorage.getModelName(provider.name) ?: when (provider) {
            AiProvider.GEMINI -> "gemini-3.5-flash"
            AiProvider.OPENAI -> "gpt-4o-mini"
            AiProvider.BYTEPLUS -> "ep-20250212104526-v2v5w"
        }
    }

    // Cached Suggestions
    suspend fun getCachedSuggestion(date: String): DailySuggestion? = healthDao.getSuggestionByDate(date)
    suspend fun saveSuggestion(suggestion: DailySuggestion) = healthDao.insertSuggestion(suggestion)
}
