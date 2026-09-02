package com.dimje.domain.model

sealed interface ComfortResponseResult {
    data class Success(
        val response: String,
        val riskLevel: WorryRiskLevel,
        val isGenerated: Boolean,
    ) : ComfortResponseResult

    data class Invalid(
        val message: String,
    ) : ComfortResponseResult

    data class Unknown(
        val message: String,
    ) : ComfortResponseResult
}
