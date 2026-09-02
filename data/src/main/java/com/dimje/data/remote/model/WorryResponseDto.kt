package com.dimje.data.remote.model

data class WorryResponseDto(
    val status: String?,
    val riskLevel: String?,
    val response: String?,
    val message: String?,
    val isGenerated: Boolean?,
)
