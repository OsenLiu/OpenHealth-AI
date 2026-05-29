package com.osen.sanoai.data.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FoodAnalysisResponse(
    val name: String,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fats: Double,
    
    // Expanded Nutrients
    val sugar: Double = 0.0,
    val fiber: Double = 0.0,
    
    // Minerals
    val calcium: Double = 0.0,
    val copper: Double = 0.0,
    val iron: Double = 0.0,
    val magnesium: Double = 0.0,
    val manganese: Double = 0.0,
    val phosphorus: Double = 0.0,
    val potassium: Double = 0.0,
    val sodium: Double = 0.0,
    val zinc: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class ExerciseAnalysisResponse(
    val name: String,
    val caloriesBurned: Double,
    val durationMinutes: Int
)

@JsonClass(generateAdapter = true)
data class HealthSuggestionResponse(
    val title: String,
    val suggestion: String,
    val mealSuggestions: List<MealSuggestion> = emptyList(),
    val exerciseSuggestions: List<ExerciseSuggestion> = emptyList()
)

@JsonClass(generateAdapter = true)
data class MealSuggestion(
    val type: String, // e.g., "Breakfast", "Lunch", "Dinner"
    val name: String,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fats: Double,
    val description: String,
    val tags: String
)

@JsonClass(generateAdapter = true)
data class ExerciseSuggestion(
    val name: String,
    val caloriesBurned: Double,
    val durationMinutes: Int,
    val intensity: String
)
