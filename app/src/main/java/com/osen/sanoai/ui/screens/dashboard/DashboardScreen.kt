package com.osen.sanoai.ui.screens.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.data.local.entities.ExerciseLog
import com.osen.sanoai.data.local.entities.FoodLog
import com.osen.sanoai.ui.components.OrganicBlobShape
import com.osen.sanoai.ui.theme.*
import com.osen.sanoai.ui.viewmodel.HealthViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: HealthViewModel,
    onNavigateToFood: () -> Unit,
    onNavigateToExercise: () -> Unit,
    onNavigateToChat: () -> Unit
) {
    val suggestion by viewModel.suggestionState.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val dailySummary by viewModel.dailySummary.collectAsState()
    val foodLogs by viewModel.dailyFoodLogs.collectAsState()
    val exerciseLogs by viewModel.dailyExerciseLogs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchDailySuggestion(AiProvider.GEMINI)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SanoAI",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = VitaMindDarkBrown
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToChat) {
                        Icon(Icons.Rounded.AutoAwesome, "AI Assistant", tint = Color(0xFFD84315))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VitaMindBackground)
            )
        },
        containerColor = VitaMindBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. Date Switcher
            item {
                DateSwitcher(
                    selectedDate = selectedDate,
                    onDateSelected = { viewModel.setSelectedDate(it) }
                )
                Spacer(Modifier.height(16.dp))
            }

            // 2. Calorie Summary Card
            item {
                CalorieSummaryCard(
                    consumed = dailySummary.totalCaloriesConsumed,
                    burned = dailySummary.totalCaloriesBurned,
                    goal = 2000.0, // Assuming a default goal
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(24.dp))
            }

            // 3. Macronutrients
            item {
                Text(
                    "三大營養素攝取狀況",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = VitaMindBrown,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MacroCard("碳水化合物", "${dailySummary.totalCarbs.toInt()}g", dailySummary.carbsPercentage, Color(0xFF4CAF50), Modifier.weight(1f))
                    MacroCard("蛋白質", "${dailySummary.totalProtein.toInt()}g", dailySummary.proteinPercentage, Color(0xFF2196F3), Modifier.weight(1f))
                    MacroCard("脂肪", "${dailySummary.totalFats.toInt()}g", dailySummary.fatsPercentage, Color(0xFFFF9800), Modifier.weight(1f))
                }
                Spacer(Modifier.height(24.dp))
            }

            // 4. Diet Intake List
            item {
                SectionHeader("飲食攝取清單", "新增食物", onNavigateToFood)
            }
            items(foodLogs) { log ->
                FoodLogItem(log, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
            }

            item { Spacer(Modifier.height(16.dp)) }

            // 5. Exercise Burn List
            item {
                SectionHeader("運動消耗清單", "新增運動", onNavigateToExercise)
            }
            items(exerciseLogs) { log ->
                ExerciseLogItem(log, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
            }

            item { Spacer(Modifier.height(24.dp)) }

            // 6. AI Prescription Card
            item {
                AiPrescriptionCard(
                    suggestion = suggestion,
                    onChat = onNavigateToChat,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun DateSwitcher(selectedDate: Long, onDateSelected: (Long) -> Unit) {
    val dates = (0..6).map { i ->
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -i)
        cal.timeInMillis
    }.reversed()

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(dates) { timestamp ->
            val date = Date(timestamp)
            val day = SimpleDateFormat("dd", Locale.getDefault()).format(date)
            val month = SimpleDateFormat("MMM", Locale.getDefault()).format(date)
            val isSelected = isSameDay(timestamp, selectedDate)

            Card(
                onClick = { onDateSelected(timestamp) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color(0xFF2E7D32) else Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.width(60.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(month, fontSize = 12.sp, color = if (isSelected) Color.White else VitaMindBrown)
                    Text(day, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else VitaMindDarkBrown)
                }
            }
        }
    }
}

private fun isSameDay(t1: Long, t2: Long): Boolean {
    val c1 = Calendar.getInstance().apply { timeInMillis = t1 }
    val c2 = Calendar.getInstance().apply { timeInMillis = t2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
            c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}

@Composable
fun CalorieSummaryCard(consumed: Double, burned: Double, goal: Double, modifier: Modifier = Modifier) {
    val progress = (consumed / goal).coerceIn(0.0, 1.0).toFloat()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B3022)), // Dark Green
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("今日卡路里結算", color = Color(0xFF81C784), fontSize = 14.sp)
                    Text(
                        SimpleDateFormat("M 月 d 日 (今天)", Locale.getDefault()).format(Date()),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Text(
                        "熱量盈餘健康",
                        color = Color(0xFF81C784),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                    Canvas(modifier = Modifier.size(100.dp)) {
                        drawArc(
                            color = Color.White.copy(alpha = 0.1f),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawArc(
                            color = Color(0xFF4CAF50),
                            startAngle = -90f,
                            sweepAngle = progress * 360f,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${(progress * 100).toInt()}%", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("攝取率", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                    }
                }

                Spacer(Modifier.width(24.dp))

                Column(modifier = Modifier.weight(1f)) {
                    CalorieRow("吃進去 :", "${consumed.toInt()} / ${goal.toInt()} kcal", progress, Color(0xFF4CAF50))
                    Spacer(Modifier.height(16.dp))
                    CalorieRow("運動燒 :", "已燃燒 ${burned.toInt()} kcal", (burned / 1000).coerceIn(0.0, 1.0).toFloat(), Color(0xFF81C784))
                }
            }
        }
    }
}

@Composable
fun CalorieRow(label: String, value: String, progress: Float, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = color,
            trackColor = Color.White.copy(alpha = 0.1f),
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun MacroCard(label: String, value: String, percentage: Float, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(label, color = VitaMindBrown, fontSize = 12.sp, modifier = Modifier.weight(1f))
                Text("${(percentage * 100).toInt()}%", color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            Text(value, color = VitaMindDarkBrown, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { percentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape),
                color = color,
                trackColor = Color(0xFFF5F5F5)
            )
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = VitaMindBrown)
        TextButton(onClick = onClick, contentPadding = PaddingValues(0.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = Color(0xFF2E7D32))
                Spacer(Modifier.width(4.dp))
                Text(action, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FoodLogItem(log: FoodLog, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(log.timestamp)),
                    fontSize = 12.sp,
                    color = VitaMindBrown
                )
                Text(log.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = VitaMindDarkBrown)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Tag("High Protein", Color(0xFFE3F2FD), Color(0xFF1976D2))
                    if (log.fats < 5) Tag("Low Fat", Color(0xFFE8F5E9), Color(0xFF388E3C))
                }
            }
            Text("${log.calories.toInt()} kcal", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = VitaMindBrown)
        }
    }
}

@Composable
fun ExerciseLogItem(log: ExerciseLog, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.FitnessCenter, null, tint = Color(0xFF2E7D32))
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(log.timestamp)),
                    fontSize = 12.sp,
                    color = VitaMindBrown
                )
                Text("${log.name} (${log.durationMinutes}分鐘)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = VitaMindDarkBrown)
            }
            Text("-${log.caloriesBurned.toInt()} kcal", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD84315))
        }
    }
}

@Composable
fun Tag(label: String, bgColor: Color, textColor: Color) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            label,
            color = textColor,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun AiPrescriptionCard(suggestion: String, onChat: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9).copy(alpha = 0.7f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Color(0xFF66BB6A), shape = RoundedCornerShape(12.dp), modifier = Modifier.size(40.dp)) {
                         // Placeholder for logo
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("Gemini 智能膳食處方", fontWeight = FontWeight.Bold, color = VitaMindDarkBrown)
                }
                Surface(color = Color(0xFFC8E6C9), shape = RoundedCornerShape(4.dp)) {
                    Text("動態深度分析", fontSize = 10.sp, color = Color(0xFF2E7D32), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(suggestion, style = MaterialTheme.typography.bodyMedium, color = VitaMindDarkBrown)
            Spacer(Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("基於本機 API 連接安全運行", fontSize = 11.sp, color = VitaMindBrown.copy(alpha = 0.6f))
                TextButton(onClick = onChat, contentPadding = PaddingValues(0.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("對話尋求更深建議", color = Color(0xFF2E7D32), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, null, modifier = Modifier.size(16.dp), tint = Color(0xFF2E7D32))
                    }
                }
            }
        }
    }
}
