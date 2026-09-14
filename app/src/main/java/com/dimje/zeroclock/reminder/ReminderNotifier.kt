package com.dimje.zeroclock.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.dimje.zeroclock.MainActivity
import com.dimje.zeroclock.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReminderNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun canNotify(): Boolean {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) return false
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "밤의 마음 리마인드", NotificationManager.IMPORTANCE_LOW).apply {
                description = "오늘 마음을 기록하지 않은 날의 조용한 안내"
                setSound(null, null)
                enableVibration(false)
            },
        )
        return NotificationManagerCompat.from(context).areNotificationsEnabled() &&
            manager.getNotificationChannel(CHANNEL_ID).importance != NotificationManager.IMPORTANCE_NONE
    }

    fun show() {
        val openIntent = Intent(context, MainActivity::class.java)
            .setAction(ACTION_OPEN_REMINDER)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_reminder_star)
            .setContentTitle("오늘의 마음을 내려놓을 시간")
            .setContentText("잠들기 전, 마음에 남은 이야기를 들려주세요.")
            .setContentIntent(PendingIntent.getActivity(
                context, 2300, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            ))
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .build()
        context.getSystemService(NotificationManager::class.java).notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "night_reminder"
        const val NOTIFICATION_ID = 2300
        const val ACTION_OPEN_REMINDER = "com.dimje.zeroclock.OPEN_REMINDER"
    }
}
