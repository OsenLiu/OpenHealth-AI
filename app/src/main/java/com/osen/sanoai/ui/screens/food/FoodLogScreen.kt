package com.osen.sanoai.ui.screens.food

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.data.local.entities.FoodLog
import com.osen.sanoai.ui.components.OrganicBlobShape
import com.osen.sanoai.ui.components.VitaTextField
import com.osen.sanoai.ui.theme.*
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
    
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fats by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Log Food", color = VitaMindDarkBrown) },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(OrganicBlobShape())
                    .background(VitaMindMint.copy(alpha = 0.5f))
            ) {
                if (capturedBitmap == null) {
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
                        modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, "Take Photo", tint = Color.White, modifier = Modifier.size(48.dp))
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
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    OutlinedButton(
                        onClick = { capturedBitmap = null },
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Retake 📸", color = VitaMindDarkBrown)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            VitaTextField(value = name, onValueChange = { name = it }, label = "Food Name")
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VitaTextField(value = calories, onValueChange = { calories = it }, label = "Kcal", modifier = Modifier.weight(1f))
                VitaTextField(value = protein, onValueChange = { protein = it }, label = "Protein", modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                VitaTextField(value = carbs, onValueChange = { carbs = it }, label = "Carbs", modifier = Modifier.weight(1f))
                VitaTextField(value = fats, onValueChange = { fats = it }, label = "Fats", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))
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
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VitaMindMint),
                shape = RoundedCornerShape(16.dp),
                enabled = name.isNotBlank()
            ) {
                Text("Save Food Log", color = VitaMindDarkBrown, style = MaterialTheme.typography.titleMedium)
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
