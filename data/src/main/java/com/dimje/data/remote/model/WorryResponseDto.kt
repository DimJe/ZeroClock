package com.dimje.data.remote.model

import com.google.gson.annotations.SerializedName

data class WorryResponseDto(
    @field:SerializedName("status")
    val status: String?,
    @field:SerializedName("riskLevel")
    val riskLevel: String?,
    @field:SerializedName("response")
    val response: String?,
    @field:SerializedName("message")
    val message: String?,
    @field:SerializedName("isGenerated")
    val isGenerated: Boolean?,
)
