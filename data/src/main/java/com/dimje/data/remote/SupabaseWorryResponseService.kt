package com.dimje.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

data class WorryResponseRequest(
    val worry: String,
)

data class WorryResponseDto(
    val response: String?,
)

interface SupabaseWorryResponseService {
    @POST("functions/v1/generate-worry-response")
    suspend fun generate(
        @Body request: WorryResponseRequest,
    ): WorryResponseDto
}
