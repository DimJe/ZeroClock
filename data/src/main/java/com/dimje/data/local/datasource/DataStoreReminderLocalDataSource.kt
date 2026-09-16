package com.dimje.data.local.datasource

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import javax.inject.Inject

private val Context.reminderStore by preferencesDataStore(name = "reminder")

class DataStoreReminderLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
) : ReminderLocalDataSource {
    override suspend fun claimNotificationDate(date: LocalDate): Boolean {
        // ponytail: 표시 전에 날짜를 선점하므로 중복은 막지만 프로세스가 중간에 종료되면 그날 알림은 생략될 수 있습니다.
        var claimed = false
        context.reminderStore.edit {
            if (it[LAST_NOTIFICATION_DATE] != date.toString()) {
                it[LAST_NOTIFICATION_DATE] = date.toString()
                claimed = true
            }
        }
        return claimed
    }

    private companion object {
        val LAST_NOTIFICATION_DATE = stringPreferencesKey("last_notification_date")
    }
}
