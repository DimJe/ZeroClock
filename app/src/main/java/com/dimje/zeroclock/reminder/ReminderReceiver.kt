package com.dimje.zeroclock.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.dimje.data.local.datasource.AlarmManagerReminderDataSource
import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.reminder.ReminderPolicy
import com.dimje.domain.usecase.ClaimReminderNotificationUseCase
import com.dimje.domain.usecase.ScheduleReminderUseCase
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {
    @Inject lateinit var scheduleReminder: ScheduleReminderUseCase
    @Inject lateinit var claimReminderNotification: ClaimReminderNotificationUseCase
    @Inject lateinit var notifier: ReminderNotifier
    @Inject lateinit var flowLogger: DataFlowLogger

    override fun onReceive(context: Context, intent: Intent) {
        val isReminder = intent.action == AlarmManagerReminderDataSource.ACTION_REMINDER
        if (!isReminder && intent.action !in setOf(
                Intent.ACTION_BOOT_COMPLETED,
                Intent.ACTION_MY_PACKAGE_REPLACED,
                Intent.ACTION_TIME_CHANGED,
            )
        ) return

        val now = Instant.now()
        val scheduledAt = intent.getLongExtra(AlarmManagerReminderDataSource.EXTRA_SCHEDULED_AT, -1L)
        try {
            // 오늘 알림을 생략해도 다음 날 예약은 유지합니다.
            scheduleReminder(now)
            flowLogger.log("app", "리마인드 예약 복원", "action=${intent.action}")
        } catch (error: Exception) {
            flowLogger.log("app", "리마인드 예약 실패", "error=${error::class.simpleName}")
        }
        if (!isReminder || scheduledAt < 0L || !notifier.canNotify()) return

        val pendingResult = goAsync()
        val wakeLock = context.getSystemService(PowerManager::class.java)
            .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ZeroClock:Reminder")
        // 화면을 켜지 않고 짧은 로컬 조회 동안만 CPU를 유지합니다.
        try {
            wakeLock.acquire(10_000L)
        } catch (error: Exception) {
            pendingResult.finish()
            flowLogger.log("app", "리마인드 처리 실패", "error=${error::class.simpleName}")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                withTimeout(8_000L) {
                    val date = claimReminderNotification(now, Instant.ofEpochMilli(scheduledAt))
                    if (date != null && notifier.canNotify() &&
                        ReminderPolicy.eligibleDate(Instant.now(), Instant.ofEpochMilli(scheduledAt)) == date
                    ) {
                        notifier.show()
                        flowLogger.log("app", "리마인드 알림 표시", "date=$date")
                    } else {
                        flowLogger.log("app", "리마인드 알림 생략", "reason=recorded_or_duplicate_or_outside_window")
                    }
                }
            } catch (_: TimeoutCancellationException) {
                flowLogger.log("app", "리마인드 처리 생략", "reason=timeout")
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                flowLogger.log("app", "리마인드 처리 실패", "error=${error::class.simpleName}")
            } finally {
                if (wakeLock.isHeld) wakeLock.release()
                pendingResult.finish()
            }
        }
    }
}
