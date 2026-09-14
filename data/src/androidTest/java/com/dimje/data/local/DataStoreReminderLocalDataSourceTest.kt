package com.dimje.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.dimje.data.local.datasource.DataStoreReminderLocalDataSource
import java.time.LocalDate
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DataStoreReminderLocalDataSourceTest {
    @Test
    fun 권한_안내와_동시_알림_선점은_중복되지_않고_상태가_유지된다() = runBlocking {
        // data 모듈 테스트 앱의 저장소만 사용하며 사용자 앱의 파일은 건드리지 않습니다.
        val context = ApplicationProvider.getApplicationContext<Context>()
        val source = DataStoreReminderLocalDataSource(context)
        source.consumePermissionRequest()
        assertFalse(source.consumePermissionRequest())
        val date = LocalDate.of(2099, 1, 1).plusDays(System.nanoTime() % 1_000_000)
        val claims = coroutineScope { List(10) { async { source.claimNotificationDate(date) } }.awaitAll() }
        assertEquals(1, claims.count { it })
        val reopened = DataStoreReminderLocalDataSource(context)
        assertFalse(reopened.consumePermissionRequest())
        assertFalse(reopened.claimNotificationDate(date))
        assertTrue(reopened.claimNotificationDate(date.plusDays(1)))
    }
}
