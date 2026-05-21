package com.osen.sanoai.data.repository

import com.osen.sanoai.data.api.AiProvider
import com.osen.sanoai.data.api.AiRepository
import com.osen.sanoai.data.local.dao.HealthDao
import com.osen.sanoai.data.local.entities.UserProfile
import com.osen.sanoai.data.secure.SecureStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify

class HealthRepositoryTest {

    @Mock
    private lateinit var healthDao: HealthDao

    @Mock
    private lateinit var secureStorage: SecureStorage

    @Mock
    private lateinit var aiRepository: AiRepository

    private lateinit var healthRepository: HealthRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        healthRepository = HealthRepository(healthDao, secureStorage, aiRepository)
    }

    @Test
    fun `getUserProfile returns flow from dao`() = runTest {
        val mockProfile = UserProfile(weight = 70.0, height = 175.0, bodyFat = 15.0, goal = "Maintain")
        `when`(healthDao.getUserProfile()).thenReturn(flowOf(mockProfile))

        val result = healthRepository.getUserProfile().first()

        assertEquals(mockProfile, result)
        verify(healthDao).getUserProfile()
    }

    @Test
    fun `saveUserProfile calls dao`() = runTest {
        val mockProfile = UserProfile(weight = 70.0, height = 175.0, bodyFat = 15.0, goal = "Maintain")
        
        healthRepository.saveUserProfile(mockProfile)

        verify(healthDao).insertUserProfile(mockProfile)
    }

    @Test
    fun `saveApiKey calls secureStorage`() {
        val provider = "gemini"
        val key = "test_key"
        
        healthRepository.saveApiKey(provider, key)

        verify(secureStorage).saveApiKey(provider, key)
    }

    @Test
    fun `analyzeExercise calls aiRepository`() = runTest {
        val description = "Running"
        val provider = AiProvider.GEMINI
        
        healthRepository.analyzeExercise(description, provider)

        verify(aiRepository).analyzeExercise(description, provider)
    }
}
