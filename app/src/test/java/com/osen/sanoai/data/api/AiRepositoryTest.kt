package com.osen.sanoai.data.api

import android.graphics.Bitmap
import com.osen.sanoai.data.api.client.OpenAiApi
import com.osen.sanoai.data.api.model.ChatCompletionResponse
import com.osen.sanoai.data.api.model.ChatMessageResponse
import com.osen.sanoai.data.api.model.Choice
import com.osen.sanoai.data.secure.SecureStorage
import com.osen.sanoai.di.NetworkModule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class AiRepositoryTest {

    @Mock
    private lateinit var openAiApi: OpenAiApi

    @Mock
    private lateinit var secureStorage: SecureStorage

    private lateinit var aiRepository: AiRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        aiRepository = AiRepository(openAiApi, secureStorage, NetworkModule.moshi)
    }

    @Test
    fun `analyzeExercise parses JSON correctly`() = runTest {
        val description = "Running 30 mins"
        val mockJsonResponse = """
            {
              "name": "Running",
              "caloriesBurned": 300.0,
              "durationMinutes": 30
            }
        """.trimIndent()

        `when`(secureStorage.getApiKey(any())).thenReturn("mock_key")
        
        val mockResponse = ChatCompletionResponse(
            choices = listOf(
                Choice(message = ChatMessageResponse(content = mockJsonResponse))
            )
        )

        `when`(openAiApi.getChatCompletion(any(), any(), any())).thenReturn(mockResponse)

        val result = aiRepository.analyzeExercise(description, AiProvider.OPENAI)

        assertNotNull(result)
        assertEquals("Running", result?.name)
        assertEquals(300.0, result?.caloriesBurned!!, 0.1)
        assertEquals(30, result?.durationMinutes)
    }

    @Test
    fun `generateHealthSuggestion parses JSON correctly`() = runTest {
        val mockJsonResponse = """
            {
              "title": "Stay Hydrated",
              "suggestion": "Drink at least 8 glasses of water today."
            }
        """.trimIndent()

        `when`(secureStorage.getApiKey(any())).thenReturn("mock_key")
        
        val mockResponse = ChatCompletionResponse(
            choices = listOf(
                Choice(message = ChatMessageResponse(content = mockJsonResponse))
            )
        )

        `when`(openAiApi.getChatCompletion(any(), any(), any())).thenReturn(mockResponse)

        val result = aiRepository.generateHealthSuggestion("profile", "logs", AiProvider.OPENAI)

        assertNotNull(result)
        assertEquals("Stay Hydrated", result?.title)
        assertEquals("Drink at least 8 glasses of water today.", result?.suggestion)
    }
}
