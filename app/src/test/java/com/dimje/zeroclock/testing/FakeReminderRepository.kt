package com.dimje.zeroclock.testing

import com.dimje.domain.repository.ReminderRepository
import java.time.Instant
import java.time.LocalDate

class FakeReminderRepository : ReminderRepository {
    private var permissionRequested = false
    private var lastNotificationDate: LocalDate? = null
    override fun scheduleAt(triggerAt: Instant) = Unit
    override suspend fun consumePermissionRequest(): Boolean = (!permissionRequested).also { permissionRequested = true }
    override suspend fun claimNotificationDate(date: LocalDate): Boolean = (lastNotificationDate != date).also { lastNotificationDate = date }
}
