package com.dimje.zeroclock.testing

import com.dimje.domain.model.ComfortResponseResult
import com.dimje.domain.model.WorryRiskLevel
import com.dimje.domain.repository.ComfortResponseRepository

class FakeComfortResponseRepository(
    private val response: String = "오늘도 충분히 잘해냈어요.",
    private val result: ComfortResponseResult? = null,
) : ComfortResponseRepository {
    override suspend fun generate(worry: String): ComfortResponseResult =
        result ?: ComfortResponseResult.Success(
            response = response,
            riskLevel = WorryRiskLevel.NORMAL,
            isGenerated = true,
        )
}
