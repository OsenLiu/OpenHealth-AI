package com.osen.sanoai.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.data.api.model.ChatMessage
import com.osen.sanoai.data.api.model.ExerciseAnalysisResponse
import com.osen.sanoai.data.api.model.FoodAnalysisResponse
import com.osen.sanoai.data.api.model.HealthSuggestionResponse
import com.osen.sanoai.data.backup.GoogleDriveService
import com.osen.sanoai.data.local.entities.DailySuggestion
import com.osen.sanoai.data.local.entities.ExerciseLog
import com.osen.sanoai.data.local.entities.FoodLog
import com.osen.sanoai.data.local.entities.UserProfile
import com.osen.sanoai.data.local.entities.WeightRecord
import com.osen.sanoai.data.repository.HealthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class HealthViewModel(
    val repository: HealthRepository,
    private val googleDriveService: GoogleDriveService
) : ViewModel() {

    private val _suggestionState = MutableStateFlow<HealthSuggestionResponse?>(null)
    val suggestionState: StateFlow<HealthSuggestionResponse?> = _suggestionState.asStateFlow()

    private val _selectedAiProvider = MutableStateFlow(repository.getSelectedAiProvider())
    val selectedAiProvider: StateFlow<AiProvider> = _selectedAiProvider.asStateFlow()

    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    val userProfile: StateFlow<UserProfile?> = repository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val dailyFoodLogs: StateFlow<List<FoodLog>> = _selectedDate
        .flatMapLatest { timestamp ->
            val range = getDayRange(timestamp)
            repository.getFoodLogsInRange(range.first, range.second)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyExerciseLogs: StateFlow<List<ExerciseLog>> = _selectedDate
        .flatMapLatest { timestamp ->
            val range = getDayRange(timestamp)
            repository.getExerciseLogsInRange(range.first, range.second)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailySummary: StateFlow<DailySummary> = combine(
        dailyFoodLogs,
        dailyExerciseLogs
    ) { food, exercise ->
        val totalKcal = food.sumOf { it.calories }
        val burnedKcal = exercise.filter { it.isCompleted }.sumOf { it.caloriesBurned }
        val protein = food.sumOf { it.protein }
        val carbs = food.sumOf { it.carbs }
        val fats = food.sumOf { it.fats }
        val sugar = food.sumOf { it.sugar }
        val fiber = food.sumOf { it.fiber }

        val totalGrams = protein + carbs + fats
        val (pPct, cPct, fPct) = if (totalGrams > 0) {
            Triple(
                (protein / totalGrams).toFloat(),
                (carbs / totalGrams).toFloat(),
                (fats / totalGrams).toFloat()
            )
        } else Triple(0f, 0f, 0f)

        DailySummary(
            totalCaloriesConsumed = totalKcal,
            totalCaloriesBurned = burnedKcal,
            totalProtein = protein,
            totalCarbs = carbs,
            totalFats = fats,
            totalSugar = sugar,
            totalFiber = fiber,
            proteinPercentage = pPct,
            carbsPercentage = cPct,
            fatsPercentage = fPct
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailySummary())

    val weightRecords: StateFlow<List<WeightRecord>> = repository.getAllWeightRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val foodLogs: StateFlow<List<FoodLog>> = repository.getAllFoodLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exerciseLogs: StateFlow<List<ExerciseLog>> = repository.getAllExerciseLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedDate(timestamp: Long) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        _selectedDate.value = calendar.timeInMillis
    }

    private fun getDayRange(timestamp: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endTime = calendar.timeInMillis
        
        return Pair(startTime, endTime)
    }

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

    fun updateExerciseLog(log: ExerciseLog) {
        viewModelScope.launch { repository.updateExerciseLog(log) }
    }

    fun saveApiKey(provider: String, key: String) {
        repository.saveApiKey(provider, key)
    }

    fun setSelectedAiProvider(provider: AiProvider) {
        _selectedAiProvider.value = provider
        repository.saveSelectedAiProvider(provider)
    }

    fun getSelectedModel(provider: AiProvider) = repository.getModelName(provider)
    fun setSelectedModel(provider: AiProvider, modelName: String) = repository.saveModelName(provider, modelName)

    fun getApiKey(provider: String) = repository.getApiKey(provider)

    suspend fun analyzeFood(bitmap: Bitmap, provider: AiProvider): FoodAnalysisResponse? {
        val modelName = repository.getModelName(provider)
        return repository.analyzeFood(bitmap, provider, modelName)
    }

    suspend fun analyzeFoodText(description: String, provider: AiProvider): FoodAnalysisResponse? {
        val modelName = repository.getModelName(provider)
        return repository.analyzeFoodText(description, provider, modelName)
    }

    suspend fun analyzeExercise(description: String, provider: AiProvider) : ExerciseAnalysisResponse? {
        val modelName = repository.getModelName(provider)
        return repository.analyzeExercise(description, provider, modelName)
    }

    fun fetchDailySuggestion(provider: AiProvider, date: Long, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val dateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(date))
            
            if (!forceRefresh) {
                val cached = repository.getCachedSuggestion(dateKey)
                if (cached != null) {
                    // Try to parse cached JSON if possible, or handle it as plain text if it was stored that way
                    // For now, let's assume we store the JSON in the 'suggestion' field
                    _suggestionState.value = repository.parseSuggestionJson(cached.suggestion)
                    return@launch
                }
            }

            _suggestionState.value = null // Reset while loading
            val profile = userProfile.value?.toString() ?: "No profile"
            
            // Get the daily logs specifically for this suggestion
            val range = getDayRange(date)
            val food = repository.getFoodLogsInRange(range.first, range.second).first().take(10)
            val exercise = repository.getExerciseLogsInRange(range.first, range.second).first().take(10)
            
            val logs = "Food: $food, Exercise: $exercise"
            val modelName = repository.getModelName(provider)
            val suggestionResponse = repository.generateHealthSuggestion(profile, logs, provider, modelName)
            
            if (suggestionResponse != null) {
                _suggestionState.value = suggestionResponse
                
                // We'll store the JSON string in the database
                val jsonToCache = repository.toJson(suggestionResponse)
                repository.saveSuggestion(DailySuggestion(
                    date = dateKey,
                    suggestion = jsonToCache,
                    timestamp = System.currentTimeMillis()
                ))
            } else {
                _suggestionState.value = HealthSuggestionResponse(title = "No insights", suggestion = "Could not reach AI.")
            }
        }
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
