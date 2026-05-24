package com.mobileaistudio.data.local.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "presets")
data class PresetEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String = "",
    val systemPrompt: String = "",
    val temperature: Float = 0.7f,
    val topP: Float = 0.9f,
    val topK: Int = 40,
    val maxTokens: Int = 2048,
    val frequencyPenalty: Float = 0f,
    val presencePenalty: Float = 0f,
    val repeatPenalty: Float = 1.1f,
    val stopSequences: String = "",
    val seed: Long = -1L,
    val createdAt: Long = System.currentTimeMillis()
)
