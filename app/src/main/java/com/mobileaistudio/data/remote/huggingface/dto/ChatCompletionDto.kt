package com.mobileaistudio.data.remote.huggingface.dto

import com.google.gson.annotations.SerializedName

data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessageDto>,
    val stream: Boolean = false,
    val max_tokens: Int = 2048,
    val temperature: Float = 0.7f,
    val top_p: Float = 0.9f,
    val frequency_penalty: Float = 0f,
    val presence_penalty: Float = 0f,
    val seed: Long? = null,
    val stop: List<String>? = null
)

data class ChatMessageDto(
    val role: String,
    val content: Any // String or List<ContentPart>
)

data class ContentPart(
    val type: String,
    val text: String? = null,
    @SerializedName("image_url") val imageUrl: ImageUrlDto? = null
)

data class ImageUrlDto(val url: String)

data class ChatCompletionResponse(
    val id: String = "",
    @SerializedName("object") val objectType: String = "",
    val created: Long = 0,
    val model: String = "",
    val choices: List<ChoiceDto> = emptyList(),
    val usage: UsageDto? = null
)

data class ChoiceDto(
    val index: Int = 0,
    val message: MessageDto = MessageDto(),
    @SerializedName("delta") val delta: MessageDto? = null,
    @SerializedName("finish_reason") val finishReason: String? = null
)

data class MessageDto(
    val role: String = "",
    val content: String? = null
)

data class UsageDto(
    @SerializedName("prompt_tokens") val promptTokens: Int = 0,
    @SerializedName("completion_tokens") val completionTokens: Int = 0,
    @SerializedName("total_tokens") val totalTokens: Int = 0
)

data class FeatureExtractionRequest(
    val inputs: Any,
    val model: String,
    val normalize: Boolean = false
)
