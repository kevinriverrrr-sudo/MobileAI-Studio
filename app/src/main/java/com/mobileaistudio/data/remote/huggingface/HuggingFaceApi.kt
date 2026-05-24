package com.mobileaistudio.data.remote.huggingface

import com.mobileaistudio.data.remote.huggingface.dto.*
import retrofit2.http.*

interface HuggingFaceApi {

    @GET("api/models")
    suspend fun searchModels(
        @Query("search") search: String? = null,
        @Query("filter") filter: String? = null,
        @Query("author") author: String? = null,
        @Query("sort") sort: String = "trending",
        @Query("limit") limit: Int = 30,
        @Query("direction") direction: Int = -1
    ): List<ModelSearchDto>

    @GET("api/models/{repoId}")
    suspend fun getModelInfo(@Path("repoId") repoId: String): ModelInfoDto

    @GET("api/models/{repoId}/tree/main")
    suspend fun getModelFiles(@Path("repoId") repoId: String): List<ModelFileDto>

    @GET("api/whoami-v2")
    suspend fun getWhoami(): UserInfoDto

    @GET("api/usage/inference")
    suspend fun getUsageInfo(): UsageInfoDto
}
