package com.osen.sanoai.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.osen.sanoai.ui.screens.dashboard.DashboardScreen
import com.osen.sanoai.ui.screens.exercise.ExerciseLogScreen
import com.osen.sanoai.ui.screens.food.FoodLogScreen
import com.osen.sanoai.ui.screens.settings.SettingsScreen
import com.osen.sanoai.ui.screens.chat.ChatScreen
import com.osen.sanoai.ui.viewmodel.ChatViewModel
import com.osen.sanoai.ui.viewmodel.ChatViewModelFactory
import com.osen.sanoai.ui.viewmodel.HealthViewModel

@Composable
fun SanoNavHost(viewModel: HealthViewModel) {
    val backStack = remember { mutableStateListOf<Any>(Destination.Dashboard) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentKey = backStack.lastOrNull()
                NavigationBarItem(
                    selected = currentKey == Destination.Dashboard,
                    onClick = { 
                        if (currentKey != Destination.Dashboard) {
                            backStack.clear()
                            backStack.add(Destination.Dashboard)
                        }
                    },
                    icon = { Icon(Icons.Default.Dashboard, "Dashboard") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = currentKey == Destination.FoodLog,
                    onClick = { 
                        if (currentKey != Destination.FoodLog) {
                            backStack.add(Destination.FoodLog)
                        }
                    },
                    icon = { Icon(Icons.Default.Restaurant, "Food") },
                    label = { Text("Food") }
                )
                NavigationBarItem(
                    selected = currentKey == Destination.ExerciseLog,
                    onClick = { 
                        if (currentKey != Destination.ExerciseLog) {
                            backStack.add(Destination.ExerciseLog)
                        }
                    },
                    icon = { Icon(Icons.Default.FitnessCenter, "Exercise") },
                    label = { Text("Exercise") }
                )
                NavigationBarItem(
                    selected = currentKey == Destination.Chat,
                    onClick = { 
                        if (currentKey != Destination.Chat) {
                            backStack.add(Destination.Chat)
                        }
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.Chat, "Chat") },
                    label = { Text("Chat") }
                )
                NavigationBarItem(
                    selected = currentKey == Destination.Settings,
                    onClick = { 
                        if (currentKey != Destination.Settings) {
                            backStack.add(Destination.Settings)
                        }
                    },
                    icon = { Icon(Icons.Default.Settings, "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { padding ->
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            modifier = Modifier.padding(padding).fillMaxSize()
        ) { key ->
            when (key) {
                Destination.Dashboard -> NavEntry(key) {
                    DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToFood = { backStack.add(Destination.FoodLog) },
                        onNavigateToExercise = { backStack.add(Destination.ExerciseLog) }
                    )
                }
                Destination.FoodLog -> NavEntry(key) {
                    FoodLogScreen(viewModel = viewModel, onBack = { backStack.removeLastOrNull() })
                }
                Destination.ExerciseLog -> NavEntry(key) {
                    ExerciseLogScreen(viewModel = viewModel, onBack = { backStack.removeLastOrNull() })
                }
                Destination.Chat -> NavEntry(key) {
                    val chatViewModel: ChatViewModel = viewModel(
                        factory = ChatViewModelFactory(viewModel.repository)
                    )
                    ChatScreen(viewModel = chatViewModel)
                }
                Destination.Settings -> NavEntry(key) {
                    SettingsScreen(viewModel = viewModel)
                }
                else -> NavEntry(Unit) { Text("Unknown Destination") }
            }
        }
    }
}
