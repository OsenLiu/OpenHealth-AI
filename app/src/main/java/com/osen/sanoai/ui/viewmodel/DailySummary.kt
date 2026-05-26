package com.osen.sanoai.ui.viewmodel

data class DailySummary(
    val totalCaloriesConsumed: Double = 0.0,
    val totalCaloriesBurned: Double = 0.0,
    val totalProtein: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val totalFats: Double = 0.0,
    val totalSugar: Double = 0.0,
    val totalFiber: Double = 0.0,
    val carbsPercentage: Float = 0f,
    val proteinPercentage: Float = 0f,
    val fatsPercentage: Float = 0f
)
