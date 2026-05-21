package com.osen.sanoai.logic

import app.cash.turbine.test
import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.data.local.entities.UserProfile
import com.osen.sanoai.data.repository.HealthRepository
import com.osen.sanoai.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class ChatLogicTest {

    @Mock
    private lateinit var repository: HealthRepository

    private lateinit var viewModel: ChatViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        `when`(repository.getUserProfile()).thenReturn(flowOf(UserProfile(weight = 70.0, height = 175.0, bodyFat = 15.0, goal = "Stay fit")))
        `when`(repository.getAllFoodLogs()).thenReturn(flowOf(emptyList()))
        `when`(repository.getAllExerciseLogs()).thenReturn(flowOf(emptyList()))

        viewModel = ChatViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sendMessage adds user and assistant messages to state`() = runTest {
        val userMsg = "Hello AI"
        val aiResponse = "Hello User! I see you want to stay fit."
        
        `when`(repository.chatWithConsultant(eq(userMsg), any(), any(), any(), any()))
            .thenReturn(aiResponse)

        viewModel.uiState.test {
            assertEquals(0, awaitItem().messages.size)
            
            viewModel.sendMessage(userMsg, AiProvider.GEMINI)
            
            // Item after user message added and loading started
            val loadingState = awaitItem()
            assertEquals(1, loadingState.messages.size)
            assertEquals("user", loadingState.messages[0].role)
            assertEquals(true, loadingState.isLoading)
            
            // Item after AI response received
            val responseState = awaitItem()
            assertEquals(2, responseState.messages.size)
            assertEquals("assistant", responseState.messages[1].role)
            assertEquals(aiResponse, responseState.messages[1].content)
            assertEquals(false, responseState.isLoading)
        }
    }

    @Test
    fun `sendMessage injects correct profile context`() = runTest {
        val userMsg = "Tell me about my progress"
        val profile = UserProfile(weight = 80.0, height = 180.0, bodyFat = 20.0, goal = "Lose weight")
        
        `when`(repository.getUserProfile()).thenReturn(flowOf(profile))
        `when`(repository.chatWithConsultant(any(), any(), any(), any(), any())).thenReturn("Response")

        viewModel.sendMessage(userMsg, AiProvider.OPENAI)
        
        verify(repository).chatWithConsultant(
            message = eq(userMsg),
            profile = eq(profile.toString()),
            logs = any(),
            history = any(),
            provider = eq(AiProvider.OPENAI)
        )
    }
}
