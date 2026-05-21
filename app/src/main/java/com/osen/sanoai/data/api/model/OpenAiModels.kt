package com.osen.sanoai.data.api.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val response_format: ResponseFormat? = null
)

@JsonClass(generateAdapter = true)
data class ChatMessage(
    val role: String,
    val content: Any
)

@JsonClass(generateAdapter = true)
data class ContentPart(
    val type: String,
    val text: String? = null,
    val image_url: ImageUrl? = null
)

@JsonClass(generateAdapter = true)
data class ImageUrl(
    val url: String // data:image/jpeg;base64,{base64_image}
)

@JsonClass(generateAdapter = true)
data class ResponseFormat(
    val type: String
)

@JsonClass(generateAdapter = true)
data class ChatCompletionResponse(
    val choices: List<Choice>
)

@JsonClass(generateAdapter = true)
data class Choice(
    val message: ChatMessageResponse
)

@JsonClass(generateAdapter = true)
data class ChatMessageResponse(
    val content: String?
)
