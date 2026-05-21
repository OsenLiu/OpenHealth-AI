package com.osen.sanoai.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.ui.viewmodel.HealthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: HealthViewModel,
    onNavigateToFood: () -> Unit,
    onNavigateToExercise: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val foodLogs by viewModel.foodLogs.collectAsState()
    val exerciseLogs by viewModel.exerciseLogs.collectAsState()
    val scope = rememberCoroutineScope()
    var suggestion by remember { mutableStateOf("Loading suggestions...") }

    LaunchedEffect(Unit) {
        suggestion = viewModel.getSuggestion(AiProvider.GEMINI)
    }

    Scaffold(
        topBar = { LargeTopAppBar(title = { Text("SanoAI Dashboard") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("AI Daily Insight", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(suggestion)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatCard("Weight", "${profile?.weight ?: "--"} kg", Modifier.weight(1f))
                    StatCard("Body Fat", "${profile?.bodyFat ?: "--"} %", Modifier.weight(1f))
                }
            }

            item {
                Text("Recent Activity", style = MaterialTheme.typography.titleLarge)
            }

            items(foodLogs.take(3)) { log ->
                ActivityItem(Icons.Default.Restaurant, log.name, "${log.calories} kcal")
            }

            items(exerciseLogs.take(3)) { log ->
                ActivityItem(Icons.Default.FitnessCenter, log.name, "-${log.caloriesBurned} kcal")
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onNavigateToFood) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Log Food")
                    }
                    Button(onClick = onNavigateToExercise) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Log Exercise")
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActivityItem(icon: ImageVector, title: String, subtitle: String) {
    ListItem(
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = { Icon(icon, null) }
    )
}
