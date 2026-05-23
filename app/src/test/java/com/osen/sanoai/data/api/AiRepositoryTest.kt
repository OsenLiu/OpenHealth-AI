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
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
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
    fun `analyzeFood parses expanded JSON correctly`() = runTest {
        val mockBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val mockJsonResponse = """
            {
              "name": "Avocado Toast",
              "calories": 350.0,
              "protein": 12.0,
              "carbs": 45.0,
              "fats": 18.0,
              "sugar": 4.0,
              "fiber": 8.0,
              "calcium": 52.0,
              "copper": 0.2,
              "iron": 2.1,
              "magnesium": 42.0,
              "manganese": 0.4,
              "phosphorus": 95.0,
              "potassium": 450.0,
              "sodium": 320.0,
              "zinc": 0.8
            }
        """.trimIndent()

        `when`(secureStorage.getApiKey(any())).thenReturn("mock_key")
        
        val mockResponse = ChatCompletionResponse(
            choices = listOf(
                Choice(message = ChatMessageResponse(content = mockJsonResponse))
            )
        )

        `when`(openAiApi.getChatCompletion(any(), any(), any())).thenReturn(mockResponse)

        val result = aiRepository.analyzeFood(mockBitmap, AiProvider.OPENAI)

        assertNotNull(result)
        assertEquals("Avocado Toast", result?.name)
        assertEquals(350.0, result?.calories!!, 0.1)
        assertEquals(12.0, result.protein, 0.1)
        assertEquals(4.0, result.sugar, 0.1)
        assertEquals(52.0, result.calcium, 0.1)
        assertEquals(0.8, result.zinc, 0.1)
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
