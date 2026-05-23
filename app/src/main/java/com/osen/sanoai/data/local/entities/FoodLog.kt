package com.osen.sanoai.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_logs")
data class FoodLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
    val zinc: Double = 0.0,

    val timestamp: Long,
    val imagePath: String? = null
)
