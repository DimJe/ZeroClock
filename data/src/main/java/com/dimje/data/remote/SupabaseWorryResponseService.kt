package com.dimje.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

data class WorryResponseRequest(
    val worry: String,
)

data class WorryResponseDto(
    val status: String?,
    val riskLevel: String?,
    val response: String?,
    val message: String?,
    val isGenerated: Boolean?,
)

interface SupabaseWorryResponseService {
    @POST("functions/v1/generate-worry-response")
    suspend fun generate(
        @Body request: WorryResponseRequest,
    ): WorryResponseDto
}
