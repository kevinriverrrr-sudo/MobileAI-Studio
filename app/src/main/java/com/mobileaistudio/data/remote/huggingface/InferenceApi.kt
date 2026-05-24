package com.mobileaistudio.data.remote.huggingface

import com.mobileaistudio.data.remote.huggingface.dto.*
import okhttp3.ResponseBody
import retrofit2.http.*

interface InferenceApi {

    @POST("v1/chat/completions")
    suspend fun chatCompletion(@Body request: ChatCompletionRequest): ChatCompletionResponse

    @POST("v1/chat/completions")
    @Streaming
    suspend fun chatCompletionStream(@Body request: ChatCompletionRequest): ResponseBody

    @POST("v1/feature-extraction")
    suspend fun featureExtraction(@Body request: FeatureExtractionRequest): List<List<Float>>
}
