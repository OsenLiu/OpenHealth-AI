package com.osen.sanoai.ui.screens.food

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.data.local.entities.FoodLog
import com.osen.sanoai.ui.viewmodel.HealthViewModel
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

@OptIn(ExperimentalMaterial3Api::class)
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
    
    // Manual entry states
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Log Food") }, navigationIcon = {
            TextButton(onClick = onBack) { Text("Back") }
        }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (capturedBitmap == null) {
                Box(modifier = Modifier.size(300.dp)) {
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
                        Icon(Icons.Default.CameraAlt, "Take Photo", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                    }
                }
            } else {
                Image(
                    bitmap = capturedBitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(300.dp)
                )
                Button(onClick = {
                    scope.launch {
                        isAnalyzing = true
                        val result = viewModel.analyzeFood(capturedBitmap!!, AiProvider.GEMINI)
                        result?.let {
                            name = it.name
                            calories = it.calories.toString()
                            protein = it.protein.toString()
                            carbs = it.carbs.toString()
                            fats = it.fats.toString()
                        }
                        isAnalyzing = false
                    }
                }, enabled = !isAnalyzing) {
                    if (isAnalyzing) CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    else Text("AI Analyze Photo")
                }
                TextButton(onClick = { capturedBitmap = null }) { Text("Retake") }
            }

            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Food Name") }, modifier = Modifier.fillMaxWidth())
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = calories, onValueChange = { calories = it }, label = { Text("Calories") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = protein, onValueChange = { protein = it }, label = { Text("Protein") }, modifier = Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = carbs, onValueChange = { carbs = it }, label = { Text("Carbs") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = fats, onValueChange = { fats = it }, label = { Text("Fats") }, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.addFoodLog(FoodLog(
                        name = name,
                        calories = calories.toDoubleOrNull() ?: 0.0,
                        protein = protein.toDoubleOrNull() ?: 0.0,
                        carbs = carbs.toDoubleOrNull() ?: 0.0,
                        fats = fats.toDoubleOrNull() ?: 0.0,
                        timestamp = System.currentTimeMillis()
                    ))
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Food Log")
            }
        }
    }
}

fun ImageProxy.toBitmap(): Bitmap {
    val buffer: ByteBuffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}
