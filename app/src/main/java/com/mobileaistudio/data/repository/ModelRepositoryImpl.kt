package com.mobileaistudio.data.repository

import android.util.Log
import com.mobileaistudio.data.local.db.dao.ModelDao
import com.mobileaistudio.data.local.db.entities.ModelEntity
import com.mobileaistudio.data.remote.huggingface.HuggingFaceApi
import com.mobileaistudio.data.remote.huggingface.dto.*
import com.mobileaistudio.domain.model.*
import com.mobileaistudio.domain.repository.IModelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class ModelRepositoryImpl @Inject constructor(
    private val api: HuggingFaceApi,
    private val modelDao: ModelDao
) : IModelRepository {

    override fun getLocalModels(): Flow<List<AIModel>> =
        modelDao.getAllModels().map { list -> list.map { it.toDomain() } }

    override fun getLoadedModel(): Flow<AIModel?> =
        modelDao.getLoadedModel().map { it?.toDomain() }

    override suspend fun getModelById(id: String): AIModel? =
        modelDao.getModelById(id)?.toDomain()

    override suspend fun searchHuggingFace(
        query: String, filter: String?, sort: String, limit: Int
    ): List<ModelSearchDto> {
        return api.searchModels(
            search = query.ifBlank { null },
            filter = filter,
            sort = sort,
            limit = limit
        )
    }

    override suspend fun getModelDetails(repoId: String): List<GGUFVariant> {
        val files = api.getModelFiles(repoId)
        return files.filter { it.path.endsWith(".gguf") }.map { file ->
            GGUFVariant(
                fileName = file.path,
                quantization = extractQuant(file.path),
                fileSizeBytes = file.size,
                downloadUrl = "https://huggingface.co/$repoId/resolve/main/${file.path}",
                estimatedRAM = (file.size * 1.5).toLong()
            )
        }
    }

    override suspend fun saveModel(model: AIModel) {
        modelDao.insert(model.toEntity())
    }

    override suspend fun deleteModel(id: String) {
        val model = modelDao.getModelById(id)
        if (model != null) {
            try {
                val file = File(model.filePath)
                if (file.exists()) {
                    file.delete()
                    Log.i("ModelRepo", "Deleted file: ${model.filePath}")
                }
            } catch (e: Exception) {
                Log.e("ModelRepo", "Failed to delete model file", e)
            }
        }
        modelDao.deleteById(id)
    }

    override suspend fun setModelLoaded(id: String, loaded: Boolean) {
        modelDao.setLoaded(id, loaded)
    }

    override suspend fun setFavorite(id: String, favorite: Boolean) {
        modelDao.setFavorite(id, favorite)
    }

    override suspend fun updateLastUsed(id: String) {
        modelDao.updateLastUsed(id, System.currentTimeMillis())
    }

    override suspend fun getRecommendedModels(capabilities: DeviceCapabilities): List<ModelSearchDto> {
        val ramGB = capabilities.totalRamGB
        val query = when {
            ramGB >= 12f -> "gguf q4 8b instruction"
            ramGB >= 8f -> "gguf q4 7b instruction"
            ramGB >= 6f -> "gguf q4 3b instruction"
            else -> "gguf q4 1.5b instruction"
        }
        return searchHuggingFace(query, "text-generation", "downloads", 20)
    }

    private fun extractQuant(path: String): String {
        val lower = path.lowercase()
        return when {
            "q2_k" in lower -> "Q2_K"
            "q3_k" in lower -> "Q3_K"
            "q4_k_m" in lower -> "Q4_K_M"
            "q4_k_s" in lower -> "Q4_K_S"
            "q4_0" in lower -> "Q4_0"
            "q5_k_m" in lower -> "Q5_K_M"
            "q5_k_s" in lower -> "Q5_K_S"
            "q6_k" in lower -> "Q6_K"
            "q8_0" in lower -> "Q8_0"
            "f16" in lower -> "F16"
            "f32" in lower -> "F32"
            else -> "Q4_K_M"
        }
    }

    private fun ModelEntity.toDomain() = AIModel(
        id = id, repoId = repoId, fileName = fileName,
        displayName = displayName, author = author, description = description,
        quantization = quantization, fileSizeBytes = fileSizeBytes,
        filePath = filePath, parameters = parameters, architecture = architecture,
        contextLength = contextLength, downloadedAt = downloadedAt,
        lastUsedAt = lastUsedAt, isFavorite = isFavorite, isLoaded = isLoaded,
        systemPrompt = systemPrompt, gpuOffloadLayers = gpuOffloadLayers,
        contextSize = contextSize, flashAttention = flashAttention,
        threadCount = threadCount, promptTemplate = promptTemplate
    )

    private fun AIModel.toEntity() = ModelEntity(
        id = id, repoId = repoId, fileName = fileName,
        displayName = displayName, author = author, description = description,
        quantization = quantization, fileSizeBytes = fileSizeBytes,
        filePath = filePath, parameters = parameters, architecture = architecture,
        contextLength = contextLength, downloadedAt = downloadedAt,
        lastUsedAt = lastUsedAt, isFavorite = isFavorite, isLoaded = isLoaded,
        systemPrompt = systemPrompt, gpuOffloadLayers = gpuOffloadLayers,
        contextSize = contextSize, flashAttention = flashAttention,
        threadCount = threadCount, promptTemplate = promptTemplate
    )
}
