package com.osen.sanoai.data.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FoodAnalysisResponse(
    val name: String,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fats: Double
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
    val suggestion: String
)
