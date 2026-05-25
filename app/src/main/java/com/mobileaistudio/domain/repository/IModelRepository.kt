package com.mobileaistudio.domain.repository

import com.mobileaistudio.domain.model.ModelSearchResult
import com.mobileaistudio.domain.model.AIModel
import com.mobileaistudio.domain.model.GGUFVariant
import com.mobileaistudio.domain.model.DeviceCapabilities
import kotlinx.coroutines.flow.Flow

interface IModelRepository {
    fun getLocalModels(): Flow<List<AIModel>>
    fun getLoadedModel(): Flow<AIModel?>
    suspend fun getModelById(id: String): AIModel?
    suspend fun searchHuggingFace(query: String, filter: String?, sort: String, limit: Int): List<ModelSearchResult>
    suspend fun getModelDetails(repoId: String): List<GGUFVariant>
    suspend fun saveModel(model: AIModel)
    suspend fun deleteModel(id: String)
    suspend fun setModelLoaded(id: String, loaded: Boolean)
    suspend fun setFavorite(id: String, favorite: Boolean)
    suspend fun updateLastUsed(id: String)
    suspend fun getRecommendedModels(capabilities: DeviceCapabilities): List<ModelSearchResult>
}
