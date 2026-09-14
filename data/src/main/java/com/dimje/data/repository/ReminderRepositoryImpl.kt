package com.dimje.data.repository

import com.dimje.data.local.datasource.AlarmManagerReminderDataSource
import com.dimje.data.local.datasource.ReminderLocalDataSource
import com.dimje.domain.repository.ReminderRepository
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

class ReminderRepositoryImpl @Inject constructor(
    private val localDataSource: ReminderLocalDataSource,
    private val alarmDataSource: AlarmManagerReminderDataSource,
) : ReminderRepository {
    override fun scheduleAt(triggerAt: Instant) = alarmDataSource.scheduleAt(triggerAt)
    override suspend fun consumePermissionRequest(): Boolean = localDataSource.consumePermissionRequest()
    override suspend fun claimNotificationDate(date: LocalDate): Boolean = localDataSource.claimNotificationDate(date)
}
