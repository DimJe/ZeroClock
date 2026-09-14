package com.dimje.zeroclock

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.dimje.zeroclock.reminder.ReminderNotifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assume.assumeTrue
import org.junit.Test
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

class ReminderNotifierTest {
    @Test
    fun 조용한_리마인드_알림과_화면_진입_이벤트를_등록한다() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val notifier = ReminderNotifier(context)
        assumeTrue("알림 권한이 허용된 기기에서 실행합니다", notifier.canNotify())
        val manager = context.getSystemService(NotificationManager::class.java)
        try {
            notifier.show()
            val notification = withTimeout(5_000L) {
                var posted = manager.activeNotifications.firstOrNull { it.id == ReminderNotifier.NOTIFICATION_ID }
                while (posted == null) {
                    delay(50)
                    posted = manager.activeNotifications.firstOrNull { it.id == ReminderNotifier.NOTIFICATION_ID }
                }
                posted.notification
            }
            assertEquals("오늘의 마음을 내려놓을 시간", notification.extras.getString(Notification.EXTRA_TITLE))
            assertNotNull(notification.contentIntent)
            assertNull(manager.getNotificationChannel(ReminderNotifier.CHANNEL_ID).sound)
        } finally {
            manager.cancel(ReminderNotifier.NOTIFICATION_ID)
        }
    }
}
