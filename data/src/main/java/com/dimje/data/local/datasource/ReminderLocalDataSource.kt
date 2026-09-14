package com.dimje.data.local.datasource

import java.time.LocalDate

interface ReminderLocalDataSource {
    suspend fun consumePermissionRequest(): Boolean
    suspend fun claimNotificationDate(date: LocalDate): Boolean
}
