package com.dimje.domain.model

sealed interface SubmitWorryResult {
    data class Saved(
        val entry: WorryEntry,
    ) : SubmitWorryResult

    data class Rejected(
        val reason: RejectionReason,
        val message: String,
    ) : SubmitWorryResult
}
