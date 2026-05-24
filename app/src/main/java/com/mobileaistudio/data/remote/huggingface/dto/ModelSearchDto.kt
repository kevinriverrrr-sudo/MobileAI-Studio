package com.mobileaistudio.data.remote.huggingface.dto

import com.google.gson.annotations.SerializedName

data class ModelSearchDto(
    @SerializedName("_id") val id: String = "",
    @SerializedName("id") val modelId: String = "",
    @SerializedName("author") val author: String = "",
    @SerializedName("sha") val sha: String = "",
    @SerializedName("lastModified") val lastModified: String = "",
    @SerializedName("private") val isPrivate: Boolean = false,
    @SerializedName("disabled") val isDisabled: Boolean = false,
    @SerializedName("gated") val isGated: Any? = null,
    @SerializedName("pipeline_tag") val pipelineTag: String? = null,
    @SerializedName("tags") val tags: List<String> = emptyList(),
    @SerializedName("downloads") val downloads: Int = 0,
    @SerializedName("likes") val likes: Int = 0,
    @SerializedName("library_name") val libraryName: String? = null,
    @SerializedName("createdAt") val createdAt: String = "",
    @SerializedName("model-index") val modelIndex: List<Map<String, Any>>? = null,
    @SerializedName("safetensors") val safetensors: Map<String, Any>? = null,
    @SerializedName("cardData") val cardData: CardData? = null,
    @SerializedName("siblings") val siblings: List<SiblingDto> = emptyList()
)

data class CardData(
    @SerializedName("language") val language: List<String>? = null,
    @SerializedName("license") val license: String? = null,
    @SerializedName("tags") val tags: List<String>? = null,
    @SerializedName("model_name") val modelName: String? = null,
    @SerializedName("description") val description: String? = null
)

data class SiblingDto(
    @SerializedName("rfilename") val rfilename: String = "",
    @SerializedName("size") val size: Long? = null
)
