package com.osen.sanoai.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.data.api.model.FoodAnalysisResponse
import com.osen.sanoai.data.backup.GoogleDriveService
import com.osen.sanoai.data.local.entities.DailySuggestion
import com.osen.sanoai.data.local.entities.ExerciseLog
import com.osen.sanoai.data.local.entities.FoodLog
import com.osen.sanoai.data.local.entities.UserProfile
import com.osen.sanoai.data.local.entities.WeightRecord
import com.osen.sanoai.data.repository.HealthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HealthViewModel(
    val repository: HealthRepository,
    private val googleDriveService: GoogleDriveService
) : ViewModel() {

    private val _suggestionState = MutableStateFlow("Loading suggestions...")
    val suggestionState: StateFlow<String> = _suggestionState.asStateFlow()

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

    fun fetchDailySuggestion(provider: AiProvider, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            
            if (!forceRefresh) {
                val cached = repository.getCachedSuggestion(dateKey)
                if (cached != null) {
                    _suggestionState.value = cached.suggestion
                    return@launch
                }
            }

            _suggestionState.value = "Consulting AI..."
            val profile = userProfile.value?.toString() ?: "No profile"
            val logs = "Food: ${foodLogs.value.take(5)}, Exercise: ${exerciseLogs.value.take(5)}"
            val suggestionResponse = repository.generateHealthSuggestion(profile, logs, provider)
            
            val suggestionText = suggestionResponse?.suggestion ?: "Keep going!"
            _suggestionState.value = suggestionText
            
            repository.saveSuggestion(DailySuggestion(
                date = dateKey,
                suggestion = suggestionText,
                timestamp = System.currentTimeMillis()
            ))
        }
    }

    // Deprecated for fetchDailySuggestion
    suspend fun getSuggestion(provider: AiProvider): String {
        return "Please use suggestionState and fetchDailySuggestion"
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
