package com.osen.sanoai.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data object Dashboard : Destination

    @Serializable
    data object FoodLog : Destination

    @Serializable
    data object ExerciseLog : Destination

    @Serializable
    data object Settings : Destination

    @Serializable
    data object Chat : Destination
}
