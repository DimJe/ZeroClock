package com.dimje.zeroclock.testing

import com.dimje.domain.repository.OnboardingRepository

class FakeOnboardingRepository(
    var completed: Boolean = true,
    var failSaving: Boolean = false,
    var failReading: Boolean = false,
) : OnboardingRepository {
    override suspend fun isCompleted(): Boolean {
        check(!failReading) { "설정 읽기 실패" }
        return completed
    }

    override suspend fun complete() {
        check(!failSaving) { "설정 저장 실패" }
        completed = true
    }
}
