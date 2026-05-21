package com.osen.sanoai.ui.screens.exercise

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.data.local.entities.ExerciseLog
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
        topBar = { TopAppBar(title = { Text("Log Exercise") }, navigationIcon = {
            TextButton(onClick = onBack) { Text("Back") }
        }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text("What did you do?", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Describe exercise (e.g., 'Ran 5km in 30 mins')") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
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
                modifier = Modifier.fillMaxWidth(),
                enabled = description.isNotBlank() && !isAnalyzing
            ) {
                if (isAnalyzing) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                else Text("AI Estimate Calories")
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Details", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Exercise Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = caloriesBurned, onValueChange = { caloriesBurned = it }, label = { Text("Calories Burned") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (minutes)") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))
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
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) {
                Text("Save Exercise Log")
            }
        }
    }
}
