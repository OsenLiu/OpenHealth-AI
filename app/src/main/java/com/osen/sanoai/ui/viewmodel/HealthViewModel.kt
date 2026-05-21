package com.osen.sanoai.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.data.api.model.FoodAnalysisResponse
import com.osen.sanoai.data.backup.GoogleDriveService
import com.osen.sanoai.data.local.entities.ExerciseLog
import com.osen.sanoai.data.local.entities.FoodLog
import com.osen.sanoai.data.local.entities.UserProfile
import com.osen.sanoai.data.local.entities.WeightRecord
import com.osen.sanoai.data.repository.HealthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HealthViewModel(
    val repository: HealthRepository,
    private val googleDriveService: GoogleDriveService
) : ViewModel() {

    val userProfile: StateFlow<UserProfile?> = repository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val weightRecords: StateFlow<List<WeightRecord>> = repository.getAllWeightRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val foodLogs: StateFlow<List<FoodLog>> = repository.getAllFoodLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exerciseLogs: StateFlow<List<ExerciseLog>> = repository.getAllExerciseLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch { repository.saveUserProfile(profile) }
    }

    fun addWeight(weight: Double) {
        viewModelScope.launch {
            repository.addWeightRecord(WeightRecord(weight = weight, timestamp = System.currentTimeMillis()))
            userProfile.value?.let {
                saveProfile(it.copy(weight = weight))
            }
        }
    }

    fun addFoodLog(log: FoodLog) {
        viewModelScope.launch { repository.addFoodLog(log) }
    }

    fun addExerciseLog(log: ExerciseLog) {
        viewModelScope.launch { repository.addExerciseLog(log) }
    }

    fun saveApiKey(provider: String, key: String) {
        repository.saveApiKey(provider, key)
    }

    fun getApiKey(provider: String) = repository.getApiKey(provider)

    suspend fun analyzeFood(bitmap: Bitmap, provider: AiProvider): FoodAnalysisResponse? {
        return repository.analyzeFood(bitmap, provider)
    }

    suspend fun analyzeExercise(description: String, provider: AiProvider) = 
        repository.analyzeExercise(description, provider)

    suspend fun getSuggestion(provider: AiProvider): String {
        val profile = userProfile.value?.toString() ?: "No profile"
        val logs = "Food: ${foodLogs.value.take(5)}, Exercise: ${exerciseLogs.value.take(5)}"
        return repository.generateHealthSuggestion(profile, logs, provider)?.suggestion ?: "Keep going!"
    }

    fun backup(accountName: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = googleDriveService.backupDatabase(accountName)
            onResult(result)
        }
    }

    fun restore(accountName: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = googleDriveService.restoreDatabase(accountName)
            onResult(result)
        }
    }
}
