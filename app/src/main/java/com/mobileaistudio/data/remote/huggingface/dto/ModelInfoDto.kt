package com.mobileaistudio.data.remote.huggingface.dto

import com.google.gson.annotations.SerializedName

data class ModelInfoDto(
    @SerializedName("id") val id: String = "",
    @SerializedName("author") val author: String = "",
    @SerializedName("sha") val sha: String = "",
    @SerializedName("lastModified") val lastModified: String = "",
    @SerializedName("private") val isPrivate: Boolean = false,
    @SerializedName("disabled") val isDisabled: Boolean = false,
    @SerializedName("gated") val isGated: String? = null,
    @SerializedName("pipeline_tag") val pipelineTag: String? = null,
    @SerializedName("tags") val tags: List<String> = emptyList(),
    @SerializedName("downloads") val downloads: Int = 0,
    @SerializedName("likes") val likes: Int = 0,
    @SerializedName("library_name") val libraryName: String? = null,
    @SerializedName("createdAt") val createdAt: String = "",
    @SerializedName("siblings") val siblings: List<SiblingDto> = emptyList(),
    @SerializedName("cardData") val cardData: CardData? = null,
    @SerializedName("model-index") val modelIndex: List<Map<String, Any>>? = null
)

data class ModelFileDto(
    @SerializedName("type") val type: String = "",
    @SerializedName("oid") val oid: String = "",
    @SerializedName("size") val size: Long = 0,
    @SerializedName("path") val path: String = ""
)
