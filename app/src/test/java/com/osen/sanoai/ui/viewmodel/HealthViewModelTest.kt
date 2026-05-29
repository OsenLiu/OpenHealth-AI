package com.osen.sanoai.ui.viewmodel

import app.cash.turbine.test
import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.data.api.model.HealthSuggestionResponse
import com.osen.sanoai.data.backup.GoogleDriveService
import com.osen.sanoai.data.local.entities.DailySuggestion
import com.osen.sanoai.data.local.entities.ExerciseLog
import com.osen.sanoai.data.local.entities.FoodLog
import com.osen.sanoai.data.local.entities.UserProfile
import com.osen.sanoai.data.local.entities.WeightRecord
import com.osen.sanoai.data.repository.HealthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
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
class HealthViewModelTest {

    @Mock
    private lateinit var repository: HealthRepository

    @Mock
    private lateinit var googleDriveService: GoogleDriveService

    private lateinit var viewModel: HealthViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        `when`(repository.getUserProfile()).thenReturn(flowOf(null))
        `when`(repository.getAllWeightRecords()).thenReturn(flowOf(emptyList()))
        `when`(repository.getAllFoodLogs()).thenReturn(flowOf(emptyList()))
        `when`(repository.getAllExerciseLogs()).thenReturn(flowOf(emptyList()))
        
        // Mock date-range queries
        `when`(repository.getFoodLogsInRange(any(), any())).thenReturn(flowOf(emptyList()))
        `when`(repository.getExerciseLogsInRange(any(), any())).thenReturn(flowOf(emptyList()))

        viewModel = HealthViewModel(repository, googleDriveService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() = runTest {
        viewModel.userProfile.test {
            assertEquals(null, awaitItem())
        }
    }

    @Test
    fun `saveProfile calls repository`() = runTest {
        val profile = UserProfile(weight = 70.0, height = 170.0, bodyFat = 15.0, goal = "Healthy")
        viewModel.saveProfile(profile)
        advanceUntilIdle()
        verify(repository).saveUserProfile(profile)
    }

    @Test
    fun `addWeight adds record and updates profile`() = runTest {
        val profile = UserProfile(weight = 70.0, height = 170.0, bodyFat = 15.0, goal = "Healthy")
        `when`(repository.getUserProfile()).thenReturn(flowOf(profile))
        
        // Re-init viewModel to pick up the mock flow
        viewModel = HealthViewModel(repository, googleDriveService)

        // Force collection in a separate job
        backgroundScope.launch { viewModel.userProfile.collect {} }
        advanceUntilIdle()

        viewModel.addWeight(75.0)
        advanceUntilIdle()
        
        verify(repository).addWeightRecord(any())
        verify(repository).saveUserProfile(profile.copy(weight = 75.0))
    }

    @Test
    fun `fetchDailySuggestion uses cache if available`() = runTest {
        val cachedSuggestion = DailySuggestion("2026-05-20", "Cached suggestion", System.currentTimeMillis())
        `when`(repository.getCachedSuggestion(any())).thenReturn(cachedSuggestion)

        viewModel.suggestionState.test {
            assertEquals("Loading suggestions...", awaitItem())
            viewModel.fetchDailySuggestion(AiProvider.GEMINI, System.currentTimeMillis())
            assertEquals("Cached suggestion", awaitItem())
        }
    }

    @Test
    fun `fetchDailySuggestion call logic`() = runTest {
        `when`(repository.getCachedSuggestion(any())).thenReturn(null)
        val mockSuggestion = HealthSuggestionResponse("Title", "New AI suggestion")
        `when`(repository.generateHealthSuggestion(any(), any(), any())).thenReturn(mockSuggestion)

        viewModel.suggestionState.test {
            assertEquals("Loading suggestions...", awaitItem())
            viewModel.fetchDailySuggestion(AiProvider.GEMINI, System.currentTimeMillis())
            
            assertEquals("Consulting AI...", awaitItem())
            assertEquals("New AI suggestion", awaitItem())
            
            advanceUntilIdle()
            verify(repository).saveSuggestion(any())
        }
    }

    @Test
    fun `backup calls googleDriveService`() = runTest {
        val account = "test@gmail.com"
        `when`(googleDriveService.backupDatabase(any())).thenReturn(true)
        
        viewModel.backup(account) { success ->
            assertEquals(true, success)
        }
        advanceUntilIdle()
        verify(googleDriveService).backupDatabase(account)
    }

    @Test
    fun `restore calls googleDriveService`() = runTest {
        val account = "test@gmail.com"
        `when`(googleDriveService.restoreDatabase(any())).thenReturn(true)
        
        viewModel.restore(account) { success ->
            assertEquals(true, success)
        }
        advanceUntilIdle()
        verify(googleDriveService).restoreDatabase(account)
    }
}
