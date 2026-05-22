package com.osen.sanoai.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.ui.components.OrganicBlobShape
import com.osen.sanoai.ui.components.SpeechBubbleShape
import com.osen.sanoai.ui.theme.*
import com.osen.sanoai.ui.viewmodel.HealthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: HealthViewModel,
    onNavigateToFood: () -> Unit,
    onNavigateToExercise: () -> Unit
) {
    val profile by viewModel.userProfile.collectAsState()
    val suggestion by viewModel.suggestionState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchDailySuggestion(AiProvider.GEMINI)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.WbSunny,
                            contentDescription = null,
                            tint = Color(0xFFFBC02D),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("VitaMind", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = VitaMindMint.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("😊", style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Your AI Companion: Gemini",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            item {
                StatCard(
                    label = "Weight",
                    value = "${profile?.weight ?: "--"} kg",
                    color = VitaMindSkyBlue,
                    modifier = Modifier.height(140.dp)
                )
            }

            item {
                StatCard(
                    label = "Body Fat",
                    value = "${profile?.bodyFat ?: "--"} %",
                    color = VitaMindCoral,
                    modifier = Modifier.height(140.dp)
                )
            }

            item(span = { GridItemSpan(2) }) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = SpeechBubbleShape(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp).padding(bottom = 12.dp)) {
                        Text(
                            "AI Health Assistant Advice",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD84315)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(suggestion, style = MaterialTheme.typography.bodyMedium)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {},
                                colors = ButtonDefaults.buttonColors(containerColor = VitaMindCoral),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Got it! ✨", color = VitaMindDarkBrown)
                            }
                            OutlinedButton(
                                onClick = { viewModel.fetchDailySuggestion(AiProvider.GEMINI, forceRefresh = true) },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Tell me more 🌿", color = VitaMindDarkBrown)
                            }
                        }
                    }
                }
            }

            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onNavigateToFood,
                        colors = ButtonDefaults.buttonColors(containerColor = VitaMindMint),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Add, null, tint = VitaMindDarkBrown)
                        Spacer(Modifier.width(8.dp))
                        Text("Log Food", color = VitaMindDarkBrown)
                    }
                    Button(
                        onClick = onNavigateToExercise,
                        colors = ButtonDefaults.buttonColors(containerColor = VitaMindSkyBlue),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Add, null, tint = VitaMindDarkBrown)
                        Spacer(Modifier.width(8.dp))
                        Text("Log Exercise", color = VitaMindDarkBrown)
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = OrganicBlobShape(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge, color = VitaMindBrown)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = VitaMindDarkBrown)
        }
    }
}
