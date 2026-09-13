package com.dimje.data.local.datasource

interface OnboardingLocalDataSource {
    suspend fun isCompleted(): Boolean
    suspend fun complete()
}
