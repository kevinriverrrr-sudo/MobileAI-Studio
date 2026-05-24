package com.mobileaistudio.data.local.db.entities

import androidx.room.*

@Entity(tableName = "models")
data class ModelEntity(
    @PrimaryKey val id: String,
    val repoId: String,
    val fileName: String,
    val displayName: String,
    val author: String,
    val description: String = "",
    val quantization: String = "",
    val fileSizeBytes: Long = 0,
    val filePath: String = "",
    val parameters: String = "",
    val architecture: String = "",
    val contextLength: Int = 0,
    val downloadedAt: Long = System.currentTimeMillis(),
    val lastUsedAt: Long = 0,
    val isFavorite: Boolean = false,
    val isLoaded: Boolean = false,
    val systemPrompt: String = "",
    val gpuOffloadLayers: Int = -1,
    val contextSize: Int = 4096,
    val flashAttention: Boolean = true,
    val threadCount: Int = 0,
    val promptTemplate: String = "auto"
)
