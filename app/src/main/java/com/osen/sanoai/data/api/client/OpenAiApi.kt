package com.osen.sanoai.data.api.client

import com.osen.sanoai.data.api.model.ChatCompletionRequest
import com.osen.sanoai.data.api.model.ChatCompletionResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface OpenAiApi {
    @POST
    suspend fun getChatCompletion(
        @Url url: String,
        @Header("Authorization") authorization: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}
