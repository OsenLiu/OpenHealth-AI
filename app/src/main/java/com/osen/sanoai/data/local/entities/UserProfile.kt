package com.osen.sanoai.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Single user app
    val weight: Double,
    val height: Double,
    val bodyFat: Double,
    val goal: String
)
