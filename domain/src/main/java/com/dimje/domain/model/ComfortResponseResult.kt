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

sealed interface SubmitWorryResult {
    data class Saved(
        val entry: WorryEntry,
    ) : SubmitWorryResult

    data class Rejected(
        val reason: RejectionReason,
        val message: String,
    ) : SubmitWorryResult
}

enum class RejectionReason {
    INVALID,
    UNKNOWN,
}
