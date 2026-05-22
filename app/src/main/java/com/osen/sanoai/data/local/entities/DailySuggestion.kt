package com.osen.sanoai.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_suggestions")
data class DailySuggestion(
    @PrimaryKey val date: String, // Format: YYYY-MM-DD
    val suggestion: String,
    val timestamp: Long
)
