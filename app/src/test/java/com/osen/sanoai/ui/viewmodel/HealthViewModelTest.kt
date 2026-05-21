package com.osen.sanoai.ui.viewmodel

import app.cash.turbine.test
import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.data.api.model.HealthSuggestionResponse
import com.osen.sanoai.data.backup.GoogleDriveService
import com.osen.sanoai.data.local.entities.ExerciseLog
import com.osen.sanoai.data.local.entities.FoodLog
import com.osen.sanoai.data.local.entities.UserProfile
import com.osen.sanoai.data.local.entities.WeightRecord
import com.osen.sanoai.data.repository.HealthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
import org.mockito.kotlin.verify

@OptIn(ExperimentalCoroutinesApi::class)
class HealthViewModelTest {

    @Mock
    private lateinit var repository: HealthRepository

    @Mock
    private lateinit var googleDriveService: GoogleDriveService

    private lateinit var viewModel: HealthViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        `when`(repository.getUserProfile()).thenReturn(flowOf(null))
        `when`(repository.getAllWeightRecords()).thenReturn(flowOf(emptyList()))
        `when`(repository.getAllFoodLogs()).thenReturn(flowOf(emptyList()))
        `when`(repository.getAllExerciseLogs()).thenReturn(flowOf(emptyList()))

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
        verify(repository).saveUserProfile(profile)
    }

    @Test
    fun `addWeight adds record and updates profile`() = runTest {
        val profile = UserProfile(weight = 70.0, height = 170.0, bodyFat = 15.0, goal = "Healthy")
        val profileFlow = MutableStateFlow<UserProfile?>(profile)
        `when`(repository.getUserProfile()).thenReturn(profileFlow)
        
        // Re-init viewModel to pick up the mock flow
        viewModel = HealthViewModel(repository, googleDriveService)

        // Ensure state is collected
        viewModel.userProfile.test {
            assertEquals(profile, awaitItem())
            viewModel.addWeight(75.0)
            verify(repository).addWeightRecord(any())
            verify(repository).saveUserProfile(profile.copy(weight = 75.0))
        }
    }

    @Test
    fun `getSuggestion returns suggestion from repository`() = runTest {
        val mockSuggestion = HealthSuggestionResponse("Title", "Drink water")
        `when`(repository.generateHealthSuggestion(any(), any(), any())).thenReturn(mockSuggestion)

        val result = viewModel.getSuggestion(AiProvider.GEMINI)
        
        assertEquals("Drink water", result)
    }

    @Test
    fun `backup calls googleDriveService`() = runTest {
        val account = "test@gmail.com"
        `when`(googleDriveService.backupDatabase(any())).thenReturn(true)
        
        viewModel.backup(account) { success ->
            assertEquals(true, success)
        }
        verify(googleDriveService).backupDatabase(account)
    }

    @Test
    fun `restore calls googleDriveService`() = runTest {
        val account = "test@gmail.com"
        `when`(googleDriveService.restoreDatabase(any())).thenReturn(true)
        
        viewModel.restore(account) { success ->
            assertEquals(true, success)
        }
        verify(googleDriveService).restoreDatabase(account)
    }
}
