package com.dimje.data.local.datasource

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.ComponentName
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import javax.inject.Inject
import javax.inject.Named

class AlarmManagerReminderDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named("reminderReceiver") private val receiverComponent: ComponentName,
) {
    fun scheduleAt(triggerAt: Instant) {
        val intent = Intent(ACTION_REMINDER)
            .setComponent(receiverComponent)
            .putExtra(EXTRA_SCHEDULED_AT, triggerAt.toEpochMilli())
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            2300,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        context.getSystemService(AlarmManager::class.java).setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAt.toEpochMilli(),
            pendingIntent,
        )
    }

    companion object {
        const val ACTION_REMINDER = "com.dimje.zeroclock.DAILY_REMINDER"
        const val EXTRA_SCHEDULED_AT = "scheduled_at"
    }
}
