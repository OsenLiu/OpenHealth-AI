package com.osen.sanoai.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.data.api.model.ChatMessage
import com.osen.sanoai.data.repository.HealthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false
)

class ChatViewModel(private val repository: HealthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(message: String, provider: AiProvider) {
        if (message.isBlank()) return

        val userMessage = ChatMessage(role = "user", content = message)
        val currentMessages = _uiState.value.messages.toMutableList()
        currentMessages.add(userMessage)
        
        _uiState.value = _uiState.value.copy(
            messages = currentMessages,
            isLoading = true
        )

        viewModelScope.launch {
            try {
                val profileData = repository.getUserProfile().first()
                val foodLogs = repository.getAllFoodLogs().first().take(10)
                val exerciseLogs = repository.getAllExerciseLogs().first().take(10)
                
                val profileString = profileData?.toString() ?: "No profile set"
                val logsString = "Food: $foodLogs, Exercise: $exerciseLogs"
                
                // Get history excluding the current system/context injection if any
                val history = _uiState.value.messages.filter { it.role != "system" }.takeLast(10)

                val response = repository.chatWithConsultant(
                    message = message,
                    profile = profileString,
                    logs = logsString,
                    history = history,
                    provider = provider
                )

                val updatedMessages = _uiState.value.messages.toMutableList()
                if (response != null) {
                    updatedMessages.add(ChatMessage(role = "assistant", content = response))
                } else {
                    updatedMessages.add(ChatMessage(role = "assistant", content = "Sorry, I encountered an error. Please try again."))
                }

                _uiState.value = _uiState.value.copy(
                    messages = updatedMessages,
                    isLoading = false
                )
            } catch (e: Exception) {
                val updatedMessages = _uiState.value.messages.toMutableList()
                updatedMessages.add(ChatMessage(role = "assistant", content = "An error occurred: ${e.message}"))
                _uiState.value = _uiState.value.copy(
                    messages = updatedMessages,
                    isLoading = false
                )
            }
        }
    }
}
