package com.dimje.zeroclock

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.core.app.ApplicationProvider
import com.dimje.zeroclock.reminder.ReminderNotifier
import org.junit.Assume.assumeTrue
import org.junit.Assert.assertTrue
import org.junit.Test

class ReminderNotificationTest {
    private val automation = InstrumentationRegistry.getInstrumentation().uiAutomation

    @Test
    fun 알림을_누르면_오늘의_마음_화면을_열고_중복_화면을_쌓지_않는다() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val notifier = ReminderNotifier(context)
        assumeTrue("알림 권한이 허용된 테스트 기기에서 실행합니다", notifier.canNotify())
        val manager = context.getSystemService(NotificationManager::class.java)
        try {
            context.startActivity(Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            await("앱 화면 진입") { rootPackage() == context.packageName }
            repeat(2) { index ->
                automation.waitForIdle(500L, 5_000L)
                notifier.show()
                await("알림 등록") { manager.activeNotifications.any { it.id == ReminderNotifier.NOTIFICATION_ID } }
                shell("cmd statusbar expand-notifications")
                await("알림창 전환") { rootPackage() == "com.android.systemui" }
                await("실제 알림창 클릭") {
                    find(automation.rootInActiveWindow, "오늘의 마음을 내려놓을 시간")?.let(::clickParent) == true
                }
                await("${index + 1}번째 클릭 후 작성 화면") { hasText("오늘의 마음") && rootPackage() == context.packageName }
            }
            shell("input keyevent KEYCODE_BACK")
            await("뒤로가기 한 번으로 홈 복귀") { !hasText("오늘의 마음") && rootPackage() == context.packageName }
            shell("input keyevent KEYCODE_BACK")
            await("Activity 종료") { rootPackage() != context.packageName }
            notifier.show()
            await("새 진입용 알림 등록") { manager.activeNotifications.any { it.id == ReminderNotifier.NOTIFICATION_ID } }
            shell("cmd statusbar expand-notifications")
            await("새 진입용 알림창 전환") { rootPackage() == "com.android.systemui" }
            await("Activity 없는 상태의 알림 클릭") {
                find(automation.rootInActiveWindow, "오늘의 마음을 내려놓을 시간")?.let(::clickParent) == true
            }
            await("새 Activity에서 작성 화면 진입") { hasText("오늘의 마음") && rootPackage() == context.packageName }
            shell("input keyevent KEYCODE_BACK")
            await("새 Activity에서 홈 복귀") { !hasText("오늘의 마음") && rootPackage() == context.packageName }
        } finally {
            manager.cancel(ReminderNotifier.NOTIFICATION_ID)
            shell("cmd statusbar collapse")
        }
    }

    private fun rootPackage() = automation.rootInActiveWindow?.packageName?.toString()
    private fun hasText(text: String) = find(automation.rootInActiveWindow, text) != null

    private fun find(node: AccessibilityNodeInfo?, text: String): AccessibilityNodeInfo? {
        node ?: return null
        if (node.text?.toString() == text || node.contentDescription?.toString() == text) return node
        for (index in 0 until node.childCount) find(node.getChild(index), text)?.let { return it }
        return null
    }

    private fun clickParent(node: AccessibilityNodeInfo): Boolean {
        var candidate: AccessibilityNodeInfo? = node
        while (candidate != null) {
            if (candidate.isClickable) return candidate.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            candidate = candidate.parent
        }
        return false
    }

    private fun shell(command: String) {
        android.os.ParcelFileDescriptor.AutoCloseInputStream(automation.executeShellCommand(command)).use { it.readBytes() }
    }

    private fun await(step: String, condition: () -> Boolean) {
        val deadline = android.os.SystemClock.uptimeMillis() + 10_000L
        var passed = condition()
        while (!passed && android.os.SystemClock.uptimeMillis() < deadline) {
            Thread.sleep(100L)
            passed = condition()
        }
        assertTrue("$step 실패 (전면 패키지=${rootPackage()})", passed)
        Log.i("ReminderVerification", "$step 통과")
    }
}
