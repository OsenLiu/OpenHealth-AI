package com.osen.sanoai.ui.screens.food

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.data.local.entities.FoodLog
import com.osen.sanoai.ui.components.OrganicBlobShape
import com.osen.sanoai.ui.components.VitaTextField
import com.osen.sanoai.ui.theme.*
import com.osen.sanoai.ui.viewmodel.HealthViewModel
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun FoodLogScreen(viewModel: HealthViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(LifecycleCameraController.IMAGE_CAPTURE)
            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        }
    }
    val scope = rememberCoroutineScope()

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    
    // Main States
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    
    // Macronutrients
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }
    var sugar by remember { mutableStateOf("") }
    var fiber by remember { mutableStateOf("") }
    
    // Micronutrients
    var calcium by remember { mutableStateOf("") }
    var copper by remember { mutableStateOf("") }
    var iron by remember { mutableStateOf("") }
    var magnesium by remember { mutableStateOf("") }
    var manganese by remember { mutableStateOf("") }
    var phosphorus by remember { mutableStateOf("") }
    var potassium by remember { mutableStateOf("") }
    var sodium by remember { mutableStateOf("") }
    var zinc by remember { mutableStateOf("") }

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Nutrition ✨", color = VitaMindDarkBrown) },
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Hero Photo Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(OrganicBlobShape())
                    .background(VitaMindMint.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                if (capturedBitmap == null) {
                    if (cameraPermissionState.status.isGranted) {
                        AndroidView(
                            factory = {
                                PreviewView(it).apply {
                                    this.controller = controller
                                    controller.bindToLifecycle(lifecycleOwner)
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = {
                                controller.takePicture(
                                    ContextCompat.getMainExecutor(context),
                                    object : ImageCapture.OnImageCapturedCallback() {
                                        override fun onCaptureSuccess(image: ImageProxy) {
                                            capturedBitmap = image.toBitmap()
                                            image.close()
                                        }
                                    }
                                )
                            },
                            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, "Take Photo", tint = Color.White, modifier = Modifier.size(48.dp))
                        }
                    } else {
                        Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                            Text("Request Camera Permission")
                        }
                    }
                } else {
                    Image(
                        bitmap = capturedBitmap!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            if (capturedBitmap != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            scope.launch {
                                isAnalyzing = true
                                val result = viewModel.analyzeFood(capturedBitmap!!, AiProvider.GEMINI)
                                result?.let {
                                    name = it.name
                                    calories = it.calories.toString()
                                    protein = it.protein.toString()
                                    carbs = it.carbs.toString()
                                    fats = it.fats.toString()
                                    sugar = it.sugar.toString()
                                    fiber = it.fiber.toString()
                                    calcium = it.calcium.toString()
                                    copper = it.copper.toString()
                                    iron = it.iron.toString()
                                    magnesium = it.magnesium.toString()
                                    manganese = it.manganese.toString()
                                    phosphorus = it.phosphorus.toString()
                                    potassium = it.potassium.toString()
                                    sodium = it.sodium.toString()
                                    zinc = it.zinc.toString()
                                }
                                isAnalyzing = false
                            }
                        },
                        enabled = !isAnalyzing,
                        colors = ButtonDefaults.buttonColors(containerColor = VitaMindCoral),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (isAnalyzing) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = VitaMindDarkBrown)
                        else Text("AI Analyze ✨", color = VitaMindDarkBrown)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { capturedBitmap = null },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Retake 📸", color = VitaMindDarkBrown)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Food Name & Main Metric
            VitaTextField(value = name, onValueChange = { name = it }, label = "Food Name")
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Estimated:", style = MaterialTheme.typography.bodyMedium, color = VitaMindBrown)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (calories.isEmpty()) "-- kcal" else "$calories kcal",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFFD84315),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Section 1: Macronutrients
            SectionHeader(title = "Macronutrients", color = Color(0xFF00796B))
            Spacer(modifier = Modifier.height(16.dp))
            MacronutrientRow(
                listOf(
                    NutrientItemData("Protein", "${protein.ifEmpty { "0" }}g", VitaMindSkyBlue),
                    NutrientItemData("Carbs", "${carbs.ifEmpty { "0" }}g", VitaMindMint),
                    NutrientItemData("Fats", "${fats.ifEmpty { "0" }}g", VitaMindCoral)
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            MacronutrientRow(
                listOf(
                    NutrientItemData("Sugar", "${sugar.ifEmpty { "0" }}g", VitaMindCoral.copy(alpha = 0.7f)),
                    NutrientItemData("Fiber", "${fiber.ifEmpty { "0" }}g", VitaMindMint.copy(alpha = 0.7f))
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Section 2: Minerals & Vitamins
            SectionHeader(title = "Micronutrients", color = Color(0xFF0277BD))
            Spacer(modifier = Modifier.height(16.dp))
            
            val micros = listOf(
                "Calcium (鈣)" to "${calcium.ifEmpty { "0" }}mg",
                "Copper (銅)" to "${copper.ifEmpty { "0" }}mg",
                "Iron (鐵)" to "${iron.ifEmpty { "0" }}mg",
                "Magnesium (鎂)" to "${magnesium.ifEmpty { "0" }}mg",
                "Manganese (錳)" to "${manganese.ifEmpty { "0" }}mg",
                "Phosphorus (磷)" to "${phosphorus.ifEmpty { "0" }}mg",
                "Potassium (鉀)" to "${potassium.ifEmpty { "0" }}mg",
                "Sodium (鈉)" to "${sodium.ifEmpty { "0" }}mg",
                "Zinc (鋅)" to "${zinc.ifEmpty { "0" }}mg"
            )

            micros.chunked(3).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { (name, value) ->
                        MicroCard(name, value, modifier = Modifier.weight(1f))
                    }
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Save Button
            Button(
                onClick = {
                    viewModel.addFoodLog(FoodLog(
                        name = name,
                        calories = calories.toDoubleOrNull() ?: 0.0,
                        protein = protein.toDoubleOrNull() ?: 0.0,
                        carbs = carbs.toDoubleOrNull() ?: 0.0,
                        fats = fats.toDoubleOrNull() ?: 0.0,
                        sugar = sugar.toDoubleOrNull() ?: 0.0,
                        fiber = fiber.toDoubleOrNull() ?: 0.0,
                        calcium = calcium.toDoubleOrNull() ?: 0.0,
                        copper = copper.toDoubleOrNull() ?: 0.0,
                        iron = iron.toDoubleOrNull() ?: 0.0,
                        magnesium = magnesium.toDoubleOrNull() ?: 0.0,
                        manganese = manganese.toDoubleOrNull() ?: 0.0,
                        phosphorus = phosphorus.toDoubleOrNull() ?: 0.0,
                        potassium = potassium.toDoubleOrNull() ?: 0.0,
                        sodium = sodium.toDoubleOrNull() ?: 0.0,
                        zinc = zinc.toDoubleOrNull() ?: 0.0,
                        timestamp = System.currentTimeMillis()
                    ))
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VitaMindMint),
                shape = RoundedCornerShape(16.dp),
                enabled = name.isNotBlank()
            ) {
                Text("Save Full Log", color = VitaMindDarkBrown, style = MaterialTheme.typography.titleMedium)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(4.dp, 24.dp).clip(RoundedCornerShape(2.dp)).background(color))
        Spacer(modifier = Modifier.width(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = VitaMindBrown)
    }
}

@Composable
fun MacronutrientRow(items: List<NutrientItemData>) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEach { data ->
            Card(
                modifier = Modifier.weight(1f).height(80.dp),
                shape = OrganicBlobShape(),
                colors = CardDefaults.cardColors(containerColor = data.color.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(data.label, style = MaterialTheme.typography.labelSmall, color = VitaMindBrown)
                    Text(data.value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = VitaMindDarkBrown)
                }
            }
        }
    }
}

@Composable
fun MicroCard(name: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(70.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(name, style = MaterialTheme.typography.labelSmall, color = VitaMindBrown, maxLines = 1)
            Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = VitaMindDarkBrown)
        }
    }
}

data class NutrientItemData(val label: String, val value: String, val color: Color)

fun ImageProxy.toBitmap(): Bitmap {
    val buffer: ByteBuffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
