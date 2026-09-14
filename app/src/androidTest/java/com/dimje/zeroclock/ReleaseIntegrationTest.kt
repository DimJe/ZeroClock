package com.dimje.zeroclock

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.dimje.data.di.DataProviderModule
import com.dimje.data.local.ZeroClockDatabase
import com.dimje.data.local.datasource.RoomWorryLocalDataSource
import com.dimje.data.remote.datasource.SupabaseComfortResponseRemoteDataSource
import com.dimje.data.repository.ComfortResponseRepositoryImpl
import com.dimje.data.repository.WorryRepositoryImpl
import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.model.SubmitWorryResult
import com.dimje.domain.usecase.SubmitWorryUseCase
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.time.LocalDate
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test

class ReleaseIntegrationTest {
    @Test
    fun Firebase가_초기화되고_검증_환경에서는_운영_오류를_수집하지_않는다() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val info = context.packageManager.getApplicationInfo(context.packageName, android.content.pm.PackageManager.GET_META_DATA)
        assumeTrue("수집을 끈 검증 빌드에서만 실행합니다", !info.metaData.getBoolean("firebase_crashlytics_collection_enabled"))
        assertTrue(FirebaseApp.getInstance().options.applicationId.isNotBlank())
        assertFalse(FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled)
    }

    @Test
    fun 실제_통신으로_받은_답변은_저장되고_잘못된_입력은_저장되지_않는다() = runBlocking {
        assumeTrue("실제 서버 요청은 명시적으로 실행합니다", InstrumentationRegistry.getArguments().getString("runLiveSupabase").toBoolean())
        val context = ApplicationProvider.getApplicationContext<Context>()
        // 사용자 DB 대신 테스트 전용 메모리 DB를 사용합니다.
        val database = Room.inMemoryDatabaseBuilder(context, ZeroClockDatabase::class.java).build()
        try {
            val repository = WorryRepositoryImpl(RoomWorryLocalDataSource(database.worryDao()))
            val retrofit = DataProviderModule.provideRetrofit(DataProviderModule.provideOkHttpClient())
            val service = DataProviderModule.provideWorryResponseService(retrofit)
            val remote = SupabaseComfortResponseRemoteDataSource(service, DataFlowLogger.NONE)
            val submit = SubmitWorryUseCase(repository, ComfortResponseRepositoryImpl(remote))
            val date = LocalDate.of(2099, 1, 1)
            val result = submit("내일 발표를 잘할 수 있을지 걱정돼요.", date)
            assertTrue("서버가 유효한 위로 답변을 반환해야 합니다", result is SubmitWorryResult.Saved)
            result as SubmitWorryResult.Saved
            assertTrue(result.entry.response.isNotBlank())
            assertEquals(result.entry, repository.getByDate(date))
            val invalidDate = date.plusDays(1)
            assertTrue(submit("asdf", invalidDate) is SubmitWorryResult.Rejected)
            assertNull(repository.getByDate(invalidDate))
        } finally {
            database.close()
        }
    }
}
