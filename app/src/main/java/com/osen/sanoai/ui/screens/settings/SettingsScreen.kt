package com.osen.sanoai.ui.screens.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.data.local.entities.UserProfile
import com.osen.sanoai.data.secure.SecureStorage
import com.osen.sanoai.ui.components.VitaTextField
import com.osen.sanoai.ui.theme.*
import com.osen.sanoai.ui.viewmodel.HealthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: HealthViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val currentAiProvider by viewModel.selectedAiProvider.collectAsState()

    var weight by remember(profile) { mutableStateOf(profile?.weight?.toString() ?: "") }
    var height by remember(profile) { mutableStateOf(profile?.height?.toString() ?: "") }
    var bodyFat by remember(profile) { mutableStateOf(profile?.bodyFat?.toString() ?: "") }
    var goal by remember(profile) { mutableStateOf(profile?.goal ?: "") }

    var selectedProvider by remember(currentAiProvider) { mutableStateOf(currentAiProvider) }
    
    // Model Selections for each provider
    var selectedGeminiModel by remember { mutableStateOf(viewModel.getSelectedModel(AiProvider.GEMINI)) }
    var selectedOpenAiModel by remember { mutableStateOf(viewModel.getSelectedModel(AiProvider.OPENAI)) }
    var selectedBytePlusModel by remember { mutableStateOf(viewModel.getSelectedModel(AiProvider.BYTEPLUS)) }

    var geminiKey by remember { mutableStateOf(viewModel.getApiKey(SecureStorage.KEY_GEMINI) ?: "") }
    var openaiKey by remember { mutableStateOf(viewModel.getApiKey(SecureStorage.KEY_OPENAI) ?: "") }
    var byteplusKey by remember { mutableStateOf(viewModel.getApiKey(SecureStorage.KEY_BYTEPLUS) ?: "") }

    val context = LocalContext.current
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(Scope(DriveScopes.DRIVE_FILE), Scope(DriveScopes.DRIVE_APPDATA))
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        if (task.isSuccessful) {
            val account = task.result
            Toast.makeText(context, "Signed in as ${account?.email}", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = VitaMindDarkBrown) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VitaMindBackground)
            )
        },
        containerColor = VitaMindBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("User Profile", style = MaterialTheme.typography.titleLarge, color = VitaMindDarkBrown)
            Spacer(modifier = Modifier.height(16.dp))
            VitaTextField(value = weight, onValueChange = { weight = it }, label = "Weight (kg)")
            Spacer(modifier = Modifier.height(12.dp))
            VitaTextField(value = height, onValueChange = { height = it }, label = "Height (cm)")
            Spacer(modifier = Modifier.height(12.dp))
            VitaTextField(value = bodyFat, onValueChange = { bodyFat = it }, label = "Body Fat (%)")
            Spacer(modifier = Modifier.height(12.dp))
            VitaTextField(value = goal, onValueChange = { goal = it }, label = "Health Goal")

            Spacer(modifier = Modifier.height(32.dp))
            Text("AI Configuration", style = MaterialTheme.typography.titleLarge, color = VitaMindDarkBrown)
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Preferred AI Provider", style = MaterialTheme.typography.titleSmall, color = VitaMindBrown)
            Spacer(modifier = Modifier.height(8.dp))
            
            AiProviderSelector(
                selectedProvider = selectedProvider,
                onProviderSelected = { selectedProvider = it }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Detailed Model Selection based on provider
            when (selectedProvider) {
                AiProvider.GEMINI -> {
                    ModelDropdownSelector(
                        label = "Gemini Model",
                        currentModel = selectedGeminiModel,
                        options = listOf("gemini-3.1-flash-lite", "gemini-3.5-flash", "gemini-2.5-pro", "gemini-2.5-flash"),
                        onModelSelected = { selectedGeminiModel = it }
                    )
                }
                AiProvider.OPENAI -> {
                    ModelDropdownSelector(
                        label = "OpenAI Model",
                        currentModel = selectedOpenAiModel,
                        options = listOf("gpt-4o", "gpt-4o-mini", "gpt-3.5-turbo"),
                        onModelSelected = { selectedOpenAiModel = it }
                    )
                }
                AiProvider.BYTEPLUS -> {
                    VitaTextField(
                        value = selectedBytePlusModel,
                        onValueChange = { selectedBytePlusModel = it },
                        label = "BytePlus Endpoint ID"
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("API Keys", style = MaterialTheme.typography.titleSmall, color = VitaMindBrown)
            Spacer(modifier = Modifier.height(12.dp))
            
            VitaTextField(
                value = geminiKey,
                onValueChange = { geminiKey = it },
                label = "Gemini API Key",
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(12.dp))
            VitaTextField(
                value = openaiKey,
                onValueChange = { openaiKey = it },
                label = "OpenAI API Key",
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(12.dp))
            VitaTextField(
                value = byteplusKey,
                onValueChange = { byteplusKey = it },
                label = "BytePlus API Key",
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text("Data Backup (Google Drive)", style = MaterialTheme.typography.titleLarge, color = VitaMindDarkBrown)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { launcher.launch(googleSignInClient.signInIntent) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VitaMindSkyBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Connect Google Account", color = VitaMindDarkBrown)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        val account = GoogleSignIn.getLastSignedInAccount(context)
                        if (account != null) {
                            viewModel.backup(account.email!!) { success ->
                                Toast.makeText(context, if (success) "Backup successful" else "Backup failed", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Please sign in first", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VitaMindMint),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Backup", color = VitaMindDarkBrown)
                }
                Button(
                    onClick = {
                        val account = GoogleSignIn.getLastSignedInAccount(context)
                        if (account != null) {
                            viewModel.restore(account.email!!) { success ->
                                Toast.makeText(context, if (success) "Restore successful. Restart app." else "Restore failed", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Please sign in first", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = VitaMindMint),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Restore", color = VitaMindDarkBrown)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {
                    viewModel.saveProfile(UserProfile(
                        weight = weight.toDoubleOrNull() ?: 0.0,
                        height = height.toDoubleOrNull() ?: 0.0,
                        bodyFat = bodyFat.toDoubleOrNull() ?: 0.0,
                        goal = goal
                    ))
                    viewModel.saveApiKey(SecureStorage.KEY_GEMINI, geminiKey)
                    viewModel.saveApiKey(SecureStorage.KEY_OPENAI, openaiKey)
                    viewModel.saveApiKey(SecureStorage.KEY_BYTEPLUS, byteplusKey)
                    
                    viewModel.setSelectedAiProvider(selectedProvider)
                    viewModel.setSelectedModel(AiProvider.GEMINI, selectedGeminiModel)
                    viewModel.setSelectedModel(AiProvider.OPENAI, selectedOpenAiModel)
                    viewModel.setSelectedModel(AiProvider.BYTEPLUS, selectedBytePlusModel)
                    
                    Toast.makeText(context, "Settings saved successfully", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VitaMindCoral),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save All Settings", color = VitaMindDarkBrown, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AiProviderSelector(
    selectedProvider: AiProvider,
    onProviderSelected: (AiProvider) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AiProvider.entries.forEach { provider ->
            val isSelected = provider == selectedProvider
            Surface(
                onClick = { onProviderSelected(provider) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) VitaMindBrown else Color.White,
                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, VitaMindBrown.copy(alpha = 0.2f))
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 12.dp)) {
                    Text(
                        text = provider.name,
                        color = if (isSelected) Color.White else VitaMindBrown,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
fun ModelDropdownSelector(
    label: String,
    currentModel: String,
    options: List<String>,
    onModelSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = VitaMindBrown)
        Spacer(modifier = Modifier.height(8.dp))
        Box {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clickable { expanded = true },
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, VitaMindBrown.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(currentModel, color = VitaMindDarkBrown)
                    Icon(Icons.Default.ArrowDropDown, null, tint = VitaMindBrown)
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.8f).background(Color.White)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = VitaMindDarkBrown) },
                        onClick = {
                            onModelSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
