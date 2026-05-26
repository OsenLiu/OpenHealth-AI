package com.osen.sanoai.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.StarOutline
import androidx.compose.material.icons.rounded.Waves
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
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
            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(32.dp)),
                tonalElevation = 8.dp,
                color = Color.White.copy(alpha = 0.9f)
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    val currentKey = backStack.lastOrNull()
                    
                    NavigationBarItem(
                        selected = currentKey == Destination.Dashboard,
                        onClick = { 
                            if (currentKey != Destination.Dashboard) {
                                backStack.clear()
                                backStack.add(Destination.Dashboard)
                            }
                        },
                        icon = { Icon(Icons.Rounded.Spa, "Home", modifier = Modifier.size(28.dp)) },
                        label = { Text("Lotus") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFFD84315),
                            indicatorColor = Color(0xFFFFE0B2)
                        )
                    )
                    NavigationBarItem(
                        selected = currentKey == Destination.FoodLog || currentKey == Destination.ExerciseLog,
                        onClick = { 
                            if (currentKey != Destination.FoodLog) {
                                backStack.add(Destination.FoodLog)
                            }
                        },
                        icon = { Icon(Icons.Rounded.Waves, "Activity", modifier = Modifier.size(28.dp)) },
                        label = { Text("Wave") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF00796B),
                            indicatorColor = Color(0xFFB2DFDB)
                        )
                    )
                    NavigationBarItem(
                        selected = currentKey == Destination.Chat,
                        onClick = { 
                            if (currentKey != Destination.Chat) {
                                backStack.add(Destination.Chat)
                            }
                        },
                        icon = { Icon(Icons.Rounded.StarOutline, "Chat", modifier = Modifier.size(28.dp)) },
                        label = { Text("Star") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF0277BD),
                            indicatorColor = Color(0xFFB3E5FC)
                        )
                    )
                    NavigationBarItem(
                        selected = currentKey == Destination.Settings,
                        onClick = { 
                            if (currentKey != Destination.Settings) {
                                backStack.add(Destination.Settings)
                            }
                        },
                        icon = { Icon(Icons.Rounded.Settings, "Settings", modifier = Modifier.size(28.dp)) },
                        label = { Text("Gear") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF5D4037),
                            indicatorColor = Color(0xFFD7CCC8)
                        )
                    )
                }
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
                        onNavigateToExercise = { backStack.add(Destination.ExerciseLog) },
                        onNavigateToChat = { backStack.add(Destination.Chat) }
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
