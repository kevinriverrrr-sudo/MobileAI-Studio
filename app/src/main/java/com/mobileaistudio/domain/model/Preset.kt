package com.mobileaistudio.domain.model

data class Preset(
    val id: String,
    val name: String,
    val description: String = "",
    val systemPrompt: String = "",
    val inferenceConfig: InferenceConfig = InferenceConfig.DEFAULT,
    val createdAt: Long = System.currentTimeMillis()
)
