package com.osen.sanoai.ui.screens.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.data.local.entities.ExerciseLog
import com.osen.sanoai.ui.components.OrganicBlobShape
import com.osen.sanoai.ui.theme.*
import com.osen.sanoai.ui.viewmodel.HealthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseLogScreen(viewModel: HealthViewModel, onBack: () -> Unit) {
    var description by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var caloriesBurned by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Exercise", color = VitaMindDarkBrown) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back", color = VitaMindBrown) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VitaMindBackground)
            )
        },
        containerColor = VitaMindBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                shape = OrganicBlobShape(),
                colors = CardDefaults.cardColors(containerColor = VitaMindSkyBlue.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth().height(180.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("What did you do?", style = MaterialTheme.typography.titleLarge, color = VitaMindDarkBrown)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Ran 5km in 30 mins...", color = VitaMindBrown.copy(alpha = 0.5f)) },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VitaMindBrown,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White.copy(alpha = 0.8f)
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    scope.launch {
                        isAnalyzing = true
                        val result = viewModel.analyzeExercise(description, AiProvider.GEMINI)
                        result?.let {
                            name = it.name
                            caloriesBurned = it.caloriesBurned.toString()
                            duration = it.durationMinutes.toString()
                        }
                        isAnalyzing = false
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = description.isNotBlank() && !isAnalyzing,
                colors = ButtonDefaults.buttonColors(containerColor = VitaMindCoral),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isAnalyzing) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = VitaMindDarkBrown)
                else Text("AI Estimate Calories ✨", color = VitaMindDarkBrown, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(40.dp))
            Text("Activity Details", style = MaterialTheme.typography.titleMedium, color = VitaMindBrown)
            Spacer(modifier = Modifier.height(16.dp))
            
            VitaExerciseField(value = name, onValueChange = { name = it }, label = "Exercise Name")
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VitaExerciseField(value = caloriesBurned, onValueChange = { caloriesBurned = it }, label = "Kcal Burned", modifier = Modifier.weight(1f))
                VitaExerciseField(value = duration, onValueChange = { duration = it }, label = "Mins", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {
                    viewModel.addExerciseLog(ExerciseLog(
                        name = name,
                        caloriesBurned = caloriesBurned.toDoubleOrNull() ?: 0.0,
                        durationMinutes = duration.toIntOrNull() ?: 0,
                        timestamp = System.currentTimeMillis()
                    ))
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VitaMindMint),
                shape = RoundedCornerShape(16.dp),
                enabled = name.isNotBlank()
            ) {
                Text("Save Activity", color = VitaMindDarkBrown, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun VitaExerciseField(value: String, onValueChange: (String) -> Unit, label: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VitaMindBrown,
            unfocusedBorderColor = VitaMindBrown.copy(alpha = 0.3f),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}
