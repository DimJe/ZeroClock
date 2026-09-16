package com.dimje.domain.repository

import java.time.Instant
import java.time.LocalDate

interface ReminderRepository {
    fun scheduleAt(triggerAt: Instant)
    suspend fun claimNotificationDate(date: LocalDate): Boolean
}
