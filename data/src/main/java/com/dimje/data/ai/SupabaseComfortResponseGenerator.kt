package com.dimje.data.ai

import com.dimje.data.remote.SupabaseWorryResponseApi
import com.dimje.domain.model.ComfortResponseResult
import com.dimje.domain.repository.ComfortResponseGenerator
import javax.inject.Inject

class SupabaseComfortResponseGenerator @Inject constructor(
    private val api: SupabaseWorryResponseApi,
) : ComfortResponseGenerator {
    override suspend fun generate(worry: String): ComfortResponseResult = api.generate(worry)
}
