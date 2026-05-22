package com.osen.sanoai.ui.screens.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import com.osen.sanoai.data.local.entities.UserProfile
import com.osen.sanoai.data.secure.SecureStorage
import com.osen.sanoai.ui.components.VitaTextField
import com.osen.sanoai.ui.theme.*
import com.osen.sanoai.ui.viewmodel.HealthViewModel
import androidx.compose.ui.text.input.PasswordVisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: HealthViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    var weight by remember { mutableStateOf(profile?.weight?.toString() ?: "") }
    var height by remember { mutableStateOf(profile?.height?.toString() ?: "") }
    var bodyFat by remember { mutableStateOf(profile?.bodyFat?.toString() ?: "") }
    var goal by remember { mutableStateOf(profile?.goal ?: "") }

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
            Toast.makeText(context, "Signed in as ${account.email}", Toast.LENGTH_SHORT).show()
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
            Text("AI API Keys", style = MaterialTheme.typography.titleLarge, color = VitaMindDarkBrown)
            Spacer(modifier = Modifier.height(16.dp))
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
