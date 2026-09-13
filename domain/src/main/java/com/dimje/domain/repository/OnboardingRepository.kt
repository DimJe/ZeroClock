package com.dimje.domain.repository

interface OnboardingRepository {
    suspend fun isCompleted(): Boolean
    suspend fun complete()
}
