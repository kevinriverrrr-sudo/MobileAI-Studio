package com.mobileaistudio.data.remote.huggingface.dto

import com.google.gson.annotations.SerializedName

data class UserInfoDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("fullname") val fullname: String = "",
    @SerializedName("avatarUrl") val avatarUrl: String? = null,
    @SerializedName("type") val type: String = "", // "user"
    @SerializedName("isPro") val isPro: Boolean = false,
    @SerializedName("canPay") val canPay: Boolean = false
)

data class UsageInfoDto(
    @SerializedName("monthlySpent") val monthlySpent: Double = 0.0,
    @SerializedName("monthlyLimit") val monthlyLimit: Double = 0.1,
    @SerializedName("resetDate") val resetDate: String? = null
)
