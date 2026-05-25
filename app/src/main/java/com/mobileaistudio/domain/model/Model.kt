package com.mobileaistudio.domain.model

data class ModelSearchResult(
    val modelId: String = "",
    val author: String = "",
    val downloads: Int = 0,
    val likes: Int = 0,
    val pipelineTag: String? = null,
    val tags: List<String> = emptyList(),
    val libraryName: String? = null
)

data class AIModel(
    val id: String,
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
    val downloadedAt: Long = 0,
    val lastUsedAt: Long = 0,
    val isFavorite: Boolean = false,
    val isLoaded: Boolean = false,
    val downloads: Int = 0,
    val likes: Int = 0,
    val pipelineTag: String? = null,
    val tags: List<String> = emptyList(),
    val systemPrompt: String = "",
    val gpuOffloadLayers: Int = -1,
    val contextSize: Int = 4096,
    val flashAttention: Boolean = true,
    val threadCount: Int = 0,
    val promptTemplate: String = "auto",
    val compatibility: ModelCompatibility = ModelCompatibility.UNKNOWN,
    val recommendedQuant: String = ""
)

enum class ModelCompatibility {
    COMPATIBLE, PARTIAL, INCOMPATIBLE, UNKNOWN
}

data class GGUFVariant(
    val fileName: String,
    val quantization: String,
    val fileSizeBytes: Long,
    val downloadUrl: String,
    val estimatedRAM: Long
)
