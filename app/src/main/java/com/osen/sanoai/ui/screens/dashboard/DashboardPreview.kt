package com.osen.sanoai.ui.screens.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.rounded.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.osen.sanoai.data.local.entities.ExerciseLog
import com.osen.sanoai.ui.theme.SanoAITheme
import com.osen.sanoai.ui.theme.VitaMindBrown
import com.osen.sanoai.ui.theme.VitaMindDarkBrown
import java.text.SimpleDateFormat
import java.util.*

@Preview(showBackground = true, backgroundColor = 0xFFFDF8F0)
@Composable
fun FinalProposedDashboardPreview() {
    val mockExerciseLogs = listOf(
        ExerciseLog(name = "戶外有氧慢跑", caloriesBurned = 330.0, durationMinutes = 35, timestamp = System.currentTimeMillis()),
        ExerciseLog(name = "徒手核心阻力訓練", caloriesBurned = 120.0, durationMinutes = 15, timestamp = System.currentTimeMillis())
    )

    SanoAITheme {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Dark Summary Card
            item {
                CalorieSummaryCard(
                    selectedDate = System.currentTimeMillis(),
                    consumed = 1930.0,
                    burned = 0.0,
                    goal = 2000.0
                )
            }

            // Exercise Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Rounded.DirectionsRun, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "當天運動計畫 (點擊勾選完成)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF616161)
                        )
                    }
                    Surface(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "已完成 0/2",
                            fontSize = 11.sp,
                            color = Color(0xFF9E9E9E),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    mockExerciseLogs.forEach { log ->
                        ExerciseChecklistItem(log = log)
                    }
                }
            }

            // AI Suggested Diet Menu Section
            item {
                AiSuggestedDietCard()
            }
        }
    }
}

@Composable
fun ExerciseChecklistItem(log: ExerciseLog) {
    var checked by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = { checked = it },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF4CAF50))
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${log.name} (${log.durationMinutes}分鐘)",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121),
                    fontSize = 15.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Timer, null, tint = Color(0xFFBDBDBD), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "預估燃燒 ${log.caloriesBurned.toInt()} kcal",
                        fontSize = 12.sp,
                        color = Color(0xFFBDBDBD)
                    )
                }
            }
            Icon(
                if (log.name.contains("慢跑")) Icons.AutoMirrored.Rounded.DirectionsRun else Icons.Rounded.Favorite,
                null,
                tint = Color(0xFFE0E0E0),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun AiSuggestedDietCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
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
                    Text(
                        "精準控制卡路里",
                        fontSize = 10.sp,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            DietRecommendationItem(
                mealType = "早餐 推薦",
                menuName = "酪梨堅果奇亞籽燕麥粥",
                desc = "410 kcal • 高纖優脂",
                icon = Icons.Rounded.BakeryDining
            )
            Spacer(Modifier.height(12.dp))
            DietRecommendationItem(
                mealType = "午餐 推薦",
                menuName = "嫩煎鮭魚配糙米飯佐西藍花",
                desc = "620 kcal • 高蛋白 Omega-3",
                icon = Icons.Rounded.SetMeal
            )
            Spacer(Modifier.height(12.dp))
            DietRecommendationItem(
                mealType = "晚餐 推薦",
                menuName = "蒜香舒肥雞胸肉配五穀米",
                desc = "350 kcal • 低脂高蛋白",
                icon = Icons.Rounded.Fastfood
            )
        }
    }
}

@Composable
fun DietRecommendationItem(mealType: String, menuName: String, desc: String, icon: ImageVector) {
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
                Text(desc, fontSize = 11.sp, color = Color(0xFF9E9E9E))
            }
            Surface(
                color = Color(0xFF4CAF50),
                shape = CircleShape,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Add, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
