package com.osen.sanoai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.viewmodel.compose.viewModel
import com.osen.sanoai.ui.navigation.SanoNavHost
import com.osen.sanoai.ui.theme.SanoAITheme
import com.osen.sanoai.ui.viewmodel.HealthViewModel
import com.osen.sanoai.ui.viewmodel.HealthViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        enableEdgeToEdge()
        
        val app = application as SanoApplication
        
        setContent {
            SanoAITheme {
                val viewModel: HealthViewModel = viewModel(
                    factory = HealthViewModelFactory(app.repository, app.googleDriveService)
                )
                SanoNavHost(viewModel = viewModel)
            }
        }
    }
}
