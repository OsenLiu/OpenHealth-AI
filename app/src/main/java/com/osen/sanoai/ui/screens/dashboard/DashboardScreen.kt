package com.osen.sanoai.ui.screens.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.data.local.entities.ExerciseLog
import com.osen.sanoai.data.local.entities.FoodLog
import com.osen.sanoai.ui.components.OrganicBlobShape
import com.osen.sanoai.ui.components.SpeechBubbleShape
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

    var showDatePicker by remember { mutableStateOf(false) }

    val dateStr = remember(selectedDate) {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(selectedDate))
    }

    LaunchedEffect(selectedDate) {
        viewModel.fetchDailySuggestion(AiProvider.GEMINI, selectedDate)
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        viewModel.setSelectedDate(it)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "SanoAI Dashboard",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = VitaMindDarkBrown
                        )
                        Text(
                            text = dateStr,
                            style = MaterialTheme.typography.labelSmall,
                            color = VitaMindBrown
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Rounded.CalendarMonth, "Select Date", tint = VitaMindBrown)
                    }
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
                    selectedDate = selectedDate,
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.DirectionsRun, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "當天運動計畫 (點擊勾選完成)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = VitaMindBrown,
                            maxLines = 1
                        )
                    }
                    val completedCount = exerciseLogs.count { it.isCompleted }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.05f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "已完成 $completedCount/${exerciseLogs.size}",
                                fontSize = 11.sp,
                                color = VitaMindBrown.copy(alpha = 0.6f),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                maxLines = 1
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        IconButton(onClick = onNavigateToExercise, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Add, "新增運動", tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
            items(exerciseLogs) { log ->
                ExerciseChecklistItem(
                    log = log,
                    onCheckedChange = { isChecked ->
                        viewModel.updateExerciseLog(log.copy(isCompleted = isChecked))
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            item { Spacer(Modifier.height(24.dp)) }

            // 6. AI Diet Suggestion Section
            item {
                AiSuggestedDietSection(
                    suggestion = suggestion,
                    onAddFood = { name, calories, protein, carbs, fats ->
                        viewModel.addFoodLog(
                            FoodLog(
                                name = name,
                                calories = calories,
                                protein = protein,
                                carbs = carbs,
                                fats = fats,
                                timestamp = selectedDate // Or current time if preferred
                            )
                        )
                    },
                    onRefresh = { viewModel.fetchDailySuggestion(AiProvider.GEMINI, selectedDate, forceRefresh = true) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun DateSwitcher(selectedDate: Long, onDateSelected: (Long) -> Unit) {
    // Generate dates around the selected date
    val dates = remember(selectedDate) {
        (-15..15).map { i ->
            Calendar.getInstance().apply {
                timeInMillis = selectedDate
                add(Calendar.DAY_OF_YEAR, i)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
    }

    val listState = rememberLazyListState()

    // Ensure the selected date is centered when it changes
    LaunchedEffect(selectedDate) {
        // The selected date is at index 15 (center of -15..15)
        // Adjust for item width and spacing if possible, but animateScrollToItem(13) is a good approximation to show it centered
        listState.animateScrollToItem(13) 
    }

    LazyRow(
        state = listState,
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
fun CalorieSummaryCard(selectedDate: Long, consumed: Double, burned: Double, goal: Double, modifier: Modifier = Modifier) {
    val progress = (consumed / goal).coerceIn(0.0, 1.0).toFloat()
    
    val formattedDate = remember(selectedDate) {
        val isToday = isSameDay(selectedDate, System.currentTimeMillis())
        val baseFormat = SimpleDateFormat("M 月 d 日", Locale.getDefault()).format(Date(selectedDate))
        if (isToday) "$baseFormat (今天)" else baseFormat
    }

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
                        formattedDate,
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
fun DietTag(label: String, bgColor: Color, textColor: Color) {
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
                    DietTag("High Protein", Color(0xFFE3F2FD), Color(0xFF1976D2))
                    if (log.fats < 5) DietTag("Low Fat", Color(0xFFE8F5E9), Color(0xFF388E3C))
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
fun ExerciseChecklistItem(
    log: ExerciseLog,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = log.isCompleted,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${log.name} (${log.durationMinutes}分鐘)",
                    fontWeight = FontWeight.Bold,
                    color = if (log.isCompleted) Color.Gray else VitaMindDarkBrown,
                    fontSize = 15.sp,
                    textDecoration = if (log.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Timer, null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "預估燃燒 ${log.caloriesBurned.toInt()} kcal",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }
            }
            Icon(
                if (log.name.contains("跑")) Icons.AutoMirrored.Rounded.DirectionsRun else Icons.Rounded.Favorite,
                null,
                tint = Color.Black.copy(alpha = 0.05f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun AiSuggestedDietSection(
    suggestion: String,
    onAddFood: (String, Double, Double, Double, Double) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9).copy(alpha = 0.7f)),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFF4CAF50),
                        shape = CircleShape,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.Restaurant, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("AI 智慧建議飲食菜單", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20), fontSize = 16.sp)
                }
                Surface(
                    color = Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    TextButton(onClick = onRefresh, contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp), modifier = Modifier.height(24.dp)) {
                        Text(
                            "刷新建議",
                            fontSize = 10.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))

            // In a real app, we would parse the 'suggestion' string or have a structured list.
            // For now, we'll show dummy items based on the UI design provided.
            
            val suggestions = listOf(
                DietSuggestion("早餐 推薦", "酪梨堅果奇亞籽燕麥粥", 410.0, "高纖優脂", Icons.Rounded.BakeryDining, 12.0, 45.0, 15.0),
                DietSuggestion("午餐 推薦", "嫩煎鮭魚配糙米飯佐西藍花", 620.0, "高蛋白 Omega-3", Icons.Rounded.SetMeal, 35.0, 50.0, 22.0),
                DietSuggestion("晚餐 推薦", "蒜香舒肥雞胸肉配五穀米", 350.0, "低脂高蛋白", Icons.Rounded.Fastfood, 30.0, 40.0, 8.0)
            )

            suggestions.forEach { item ->
                DietRecommendationItem(
                    mealType = item.type,
                    menuName = item.name,
                    desc = "${item.kcal.toInt()} kcal • ${item.tags}",
                    icon = item.icon,
                    onAdd = {
                        onAddFood(item.name, item.kcal, item.protein, item.carbs, item.fats)
                    }
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

data class DietSuggestion(
    val type: String,
    val name: String,
    val kcal: Double,
    val tags: String,
    val icon: ImageVector,
    val protein: Double,
    val carbs: Double,
    val fats: Double
)

@Composable
fun DietRecommendationItem(mealType: String, menuName: String, desc: String, icon: ImageVector, onAdd: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(24.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(mealType, fontSize = 11.sp, color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                Text(menuName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF212121))
                Text(desc, fontSize = 11.sp, color = Color.LightGray)
            }
            IconButton(
                onClick = onAdd,
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFF4CAF50), CircleShape)
            ) {
                Icon(Icons.Rounded.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}
