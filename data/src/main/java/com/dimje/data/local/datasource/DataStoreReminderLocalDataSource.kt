package com.dimje.data.local.datasource

import android.content.Context
import android.os.Build
import androidx.datastore.preferences.core.booleanPreferencesKey
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
    override suspend fun consumePermissionRequest(): Boolean {
        // 권한 요청이 없는 구버전에서는 OS 업그레이드 후 안내할 기회를 남깁니다.
        if (Build.VERSION.SDK_INT < 33) return false
        var consumed = false
        context.reminderStore.edit {
            if (it[PERMISSION_REQUESTED] != true) {
                it[PERMISSION_REQUESTED] = true
                consumed = true
            }
        }
        return consumed
    }

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
        val PERMISSION_REQUESTED = booleanPreferencesKey("permission_requested")
        val LAST_NOTIFICATION_DATE = stringPreferencesKey("last_notification_date")
    }
}
