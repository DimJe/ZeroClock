package com.dimje.data.remote

import com.dimje.data.remote.model.WorryResponseDto
import com.dimje.data.remote.model.WorryResponseRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface SupabaseWorryResponseService {
    @POST("functions/v1/generate-worry-response")
    suspend fun generate(
        @Body request: WorryResponseRequest,
    ): WorryResponseDto
}
