package com.mobileaistudio.domain.model

data class InferenceConfig(
    val temperature: Float = 0.7f,
    val topP: Float = 0.9f,
    val topK: Int = 40,
    val maxTokens: Int = 2048,
    val frequencyPenalty: Float = 0f,
    val presencePenalty: Float = 0f,
    val repeatPenalty: Float = 1.1f,
    val stopSequences: List<String> = emptyList(),
    val seed: Long = -1L
) {
    fun toMap(): Map<String, Any> = mapOf(
        "temperature" to temperature,
        "top_p" to topP,
        "top_k" to topK,
        "max_tokens" to maxTokens,
        "frequency_penalty" to frequencyPenalty,
        "presence_penalty" to presencePenalty,
        "repeat_penalty" to repeatPenalty
    )

    companion object {
        val DEFAULT = InferenceConfig()
    }
}
