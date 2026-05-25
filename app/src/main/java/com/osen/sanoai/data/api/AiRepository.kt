package com.osen.sanoai.data.api

import android.graphics.Bitmap
import android.util.Base64
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.osen.sanoai.data.api.client.OpenAiApi
import com.osen.sanoai.data.api.model.*
import com.osen.sanoai.data.secure.SecureStorage
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class AiRepository(
    private val openAiApi: OpenAiApi,
    private val secureStorage: SecureStorage,
    private val moshi: Moshi
) {
    private val foodAdapter = moshi.adapter(FoodAnalysisResponse::class.java)
    private val exerciseAdapter = moshi.adapter(ExerciseAnalysisResponse::class.java)
    private val suggestionAdapter = moshi.adapter(HealthSuggestionResponse::class.java)

    suspend fun analyzeFood(bitmap: Bitmap, provider: AiProvider): FoodAnalysisResponse? = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Analyze this food image. Provide the following fields in JSON format:
                {
                  "name": "...",
                  "calories": 0.0,
                  "protein": 0.0,
                  "carbs": 0.0,
                  "fats": 0.0,
                  "sugar": 0.0,
                  "fiber": 0.0,
                  "calcium": 0.0,
                  "copper": 0.0,
                  "iron": 0.0,
                  "magnesium": 0.0,
                  "manganese": 0.0,
                  "phosphorus": 0.0,
                  "potassium": 0.0,
                  "sodium": 0.0,
                  "zinc": 0.0
                }
                Only return the JSON.
            """.trimIndent()
            val json = when (provider) {
                AiProvider.GEMINI -> {
                    val apiKey = secureStorage.getApiKey(SecureStorage.KEY_GEMINI) ?: return@withContext null
                    val model = GenerativeModel(modelName = "gemini-3.5-flash", apiKey = apiKey)
                    val response = model.generateContent(content {
                        image(bitmap)
                        text(prompt)
                    })
                    response.text
                }
                AiProvider.OPENAI, AiProvider.BYTEPLUS -> {
                    val key = if (provider == AiProvider.OPENAI) SecureStorage.KEY_OPENAI else SecureStorage.KEY_BYTEPLUS
                    val apiKey = secureStorage.getApiKey(key) ?: return@withContext null
                    val baseUrl = if (provider == AiProvider.OPENAI) "https://api.openai.com/v1/chat/completions" else "https://ark.ap-southeast.bytepluses.com/api/v3/chat/completions"
                    val modelName = if (provider == AiProvider.OPENAI) "gpt-5.4-mini" else "ep-20250212104526-v2v5w"

                    val base64Image = bitmapToBase64(bitmap)
                    val request = ChatCompletionRequest(
                        model = modelName,
                        messages = listOf(
                            ChatMessage(
                                role = "user",
                                content = listOf(
                                    ContentPart(type = "text", text = prompt),
                                    ContentPart(type = "image_url", image_url = ImageUrl(url = "data:image/jpeg;base64,$base64Image"))
                                )
                            )
                        )
                    )
                    val response = openAiApi.getChatCompletion(baseUrl, "Bearer $apiKey", request)
                    response.choices.firstOrNull()?.message?.content
                }
            }
            json?.let { extractJson(it) }?.let { foodAdapter.fromJson(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun analyzeFoodText(description: String, provider: AiProvider): FoodAnalysisResponse? = withContext(Dispatchers.IO) {
        try {
            val prompt = """
                Analyze this food description: '$description'. Provide the following fields in JSON format:
                {
                  "name": "...",
                  "calories": 0.0,
                  "protein": 0.0,
                  "carbs": 0.0,
                  "fats": 0.0,
                  "sugar": 0.0,
                  "fiber": 0.0,
                  "calcium": 0.0,
                  "copper": 0.0,
                  "iron": 0.0,
                  "magnesium": 0.0,
                  "manganese": 0.0,
                  "phosphorus": 0.0,
                  "potassium": 0.0,
                  "sodium": 0.0,
                  "zinc": 0.0
                }
                Only return the JSON.
            """.trimIndent()
            val json = when (provider) {
                AiProvider.GEMINI -> {
                    val apiKey = secureStorage.getApiKey(SecureStorage.KEY_GEMINI) ?: return@withContext null
                    val model = GenerativeModel(modelName = "gemini-3.5-flash", apiKey = apiKey)
                    val response = model.generateContent(prompt)
                    response.text
                }
                AiProvider.OPENAI, AiProvider.BYTEPLUS -> {
                    val key = if (provider == AiProvider.OPENAI) SecureStorage.KEY_OPENAI else SecureStorage.KEY_BYTEPLUS
                    val apiKey = secureStorage.getApiKey(key) ?: return@withContext null
                    val baseUrl = if (provider == AiProvider.OPENAI) "https://api.openai.com/v1/chat/completions" else "https://ark.ap-southeast.bytepluses.com/api/v3/chat/completions"
                    val modelName = if (provider == AiProvider.OPENAI) "gpt-5.4-mini" else "ep-20250212104526-v2v5w"

                    val request = ChatCompletionRequest(
                        model = modelName,
                        messages = listOf(ChatMessage(role = "user", content = prompt))
                    )
                    val response = openAiApi.getChatCompletion(baseUrl, "Bearer $apiKey", request)
                    response.choices.firstOrNull()?.message?.content
                }
            }
            json?.let { extractJson(it) }?.let { foodAdapter.fromJson(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun analyzeExercise(description: String, provider: AiProvider): ExerciseAnalysisResponse? = withContext(Dispatchers.IO) {
        try {
            val prompt = "Estimate the calories burned for this exercise: '$description'. Provide the name, estimated calories burned, and duration in minutes in JSON format: { \"name\": \"...\", \"caloriesBurned\": 0.0, \"durationMinutes\": 0 }. Only return the JSON."
            val json = when (provider) {
                AiProvider.GEMINI -> {
                    val apiKey = secureStorage.getApiKey(SecureStorage.KEY_GEMINI) ?: return@withContext null
                    val model = GenerativeModel(modelName = "gemini-3.5-flash", apiKey = apiKey)
                    val response = model.generateContent(prompt)
                    response.text
                }
                AiProvider.OPENAI, AiProvider.BYTEPLUS -> {
                    val key = if (provider == AiProvider.OPENAI) SecureStorage.KEY_OPENAI else SecureStorage.KEY_BYTEPLUS
                    val apiKey = secureStorage.getApiKey(key) ?: return@withContext null
                    val baseUrl = if (provider == AiProvider.OPENAI) "https://api.openai.com/v1/chat/completions" else "https://ark.ap-southeast.bytepluses.com/api/v3/chat/completions"
                    val modelName = if (provider == AiProvider.OPENAI) "gpt-5.4-mini" else "ep-20250212104526-v2v5w"

                    val request = ChatCompletionRequest(
                        model = modelName,
                        messages = listOf(ChatMessage(role = "user", content = prompt))
                    )
                    val response = openAiApi.getChatCompletion(baseUrl, "Bearer $apiKey", request)
                    response.choices.firstOrNull()?.message?.content
                }
            }
            json?.let { extractJson(it) }?.let { exerciseAdapter.fromJson(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun generateHealthSuggestion(profile: String, logs: String, provider: AiProvider): HealthSuggestionResponse? = withContext(Dispatchers.IO) {
        try {
            val prompt = "Based on the user's profile: $profile and recent logs: $logs, provide a daily health suggestion. Format as JSON: { \"title\": \"...\", \"suggestion\": \"...\" }. Only return the JSON."
            val json = when (provider) {
                AiProvider.GEMINI -> {
                    val apiKey = secureStorage.getApiKey(SecureStorage.KEY_GEMINI) ?: return@withContext null
                    val model = GenerativeModel(modelName = "gemini-3.5-flash", apiKey = apiKey)
                    val response = model.generateContent(prompt)
                    response.text
                }
                AiProvider.OPENAI, AiProvider.BYTEPLUS -> {
                    val key = if (provider == AiProvider.OPENAI) SecureStorage.KEY_OPENAI else SecureStorage.KEY_BYTEPLUS
                    val apiKey = secureStorage.getApiKey(key) ?: return@withContext null
                    val baseUrl = if (provider == AiProvider.OPENAI) "https://api.openai.com/v1/chat/completions" else "https://ark.ap-southeast.bytepluses.com/api/v3/chat/completions"
                    val modelName = if (provider == AiProvider.OPENAI) "gpt-5.4-mini" else "ep-20250212104526-v2v5w"

                    val request = ChatCompletionRequest(
                        model = modelName,
                        messages = listOf(ChatMessage(role = "user", content = prompt))
                    )
                    val response = openAiApi.getChatCompletion(baseUrl, "Bearer $apiKey", request)
                    response.choices.firstOrNull()?.message?.content
                }
            }
            json?.let { extractJson(it) }?.let { suggestionAdapter.fromJson(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun chatWithConsultant(
        message: String,
        profile: String,
        logs: String,
        history: List<ChatMessage>,
        provider: AiProvider
    ): String? = withContext(Dispatchers.IO) {
        try {
            val systemPrompt = "You are a personalized AI Health Consultant for SanoAI. " +
                    "User Profile: $profile. Recent Logs: $logs. " +
                    "Provide helpful, concise, and evidence-based health and sports advice based on this context."

            when (provider) {
                AiProvider.GEMINI -> {
                    val apiKey = secureStorage.getApiKey(SecureStorage.KEY_GEMINI) ?: return@withContext null
                    val model = GenerativeModel(modelName = "gemini-3.5-flash", apiKey = apiKey)
                    // Simplified: Prepend system prompt to the user message for Gemini
                    val fullPrompt = "$systemPrompt\n\nUser: $message"
                    val response = model.generateContent(fullPrompt)
                    response.text
                }
                AiProvider.OPENAI, AiProvider.BYTEPLUS -> {
                    val key = if (provider == AiProvider.OPENAI) SecureStorage.KEY_OPENAI else SecureStorage.KEY_BYTEPLUS
                    val apiKey = secureStorage.getApiKey(key) ?: return@withContext null
                    val baseUrl = if (provider == AiProvider.OPENAI) "https://api.openai.com/v1/chat/completions" else "https://ark.ap-southeast.bytepluses.com/api/v3/chat/completions"
                    val modelName = if (provider == AiProvider.OPENAI) "gpt-5.4-mini" else "ep-20250212104526-v2v5w"

                    val messages = mutableListOf<ChatMessage>()
                    messages.add(ChatMessage(role = "system", content = systemPrompt))
                    messages.addAll(history)
                    messages.add(ChatMessage(role = "user", content = message))

                    val request = ChatCompletionRequest(
                        model = modelName,
                        messages = messages
                    )
                    val response = openAiApi.getChatCompletion(baseUrl, "Bearer $apiKey", request)
                    response.choices.firstOrNull()?.message?.content
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun extractJson(text: String): String {
        val start = text.indexOf("{")
        val end = text.lastIndexOf("}")
        return if (start != -1 && end != -1) {
            text.substring(start, end + 1)
        } else {
            text
        }
    }
}
