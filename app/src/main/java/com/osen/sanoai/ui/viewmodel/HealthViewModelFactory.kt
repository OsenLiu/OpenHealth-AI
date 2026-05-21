package com.osen.sanoai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.osen.sanoai.data.backup.GoogleDriveService
import com.osen.sanoai.data.repository.HealthRepository

class HealthViewModelFactory(
    private val repository: HealthRepository,
    private val googleDriveService: GoogleDriveService
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HealthViewModel::class.java)) {
            return HealthViewModel(repository, googleDriveService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
