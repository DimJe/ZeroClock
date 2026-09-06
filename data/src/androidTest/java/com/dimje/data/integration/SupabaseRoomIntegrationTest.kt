package com.dimje.data.integration

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.dimje.data.BuildConfig
import com.dimje.data.local.ZeroClockDatabase
import com.dimje.data.local.datasource.RoomWorryLocalDataSource
import com.dimje.data.remote.SupabaseWorryResponseService
import com.dimje.data.remote.datasource.SupabaseComfortResponseRemoteDataSource
import com.dimje.data.repository.ComfortResponseRepositoryImpl
import com.dimje.data.repository.WorryRepositoryImpl
import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.model.SubmitWorryResult
import com.dimje.domain.usecase.SubmitWorryUseCase
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(AndroidJUnit4::class)
class SupabaseRoomIntegrationTest {
    private lateinit var database: ZeroClockDatabase
    private lateinit var repository: WorryRepositoryImpl
    private lateinit var submitWorry: SubmitWorryUseCase

    @Before
    fun 통합_테스트를_준비한다() {
        val shouldRun = InstrumentationRegistry.getArguments()
            .getString(RUN_LIVE_ARGUMENT)
            .toBoolean()
        assumeTrue("실제 Supabase 통합 테스트는 명시적으로 요청할 때만 실행합니다.", shouldRun)

        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ZeroClockDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = WorryRepositoryImpl(RoomWorryLocalDataSource(database.worryDao()))

        val service = Retrofit.Builder()
            .baseUrl(BuildConfig.SUPABASE_BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build(),
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseWorryResponseService::class.java)
        val remoteDataSource = SupabaseComfortResponseRemoteDataSource(service, DataFlowLogger.NONE)
        submitWorry = SubmitWorryUseCase(repository, ComfortResponseRepositoryImpl(remoteDataSource))
    }

    @After
    fun 데이터베이스를_닫는다() {
        if (::database.isInitialized) {
            database.close()
        }
    }

    @Test
    fun 실제_Supabase_답변을_받아_Room에_저장하고_조회한다() = runBlocking {
        val date = LocalDate.of(2099, 1, 1)

        val result = submitWorry("내일 발표를 차분하게 잘할 수 있을지 걱정돼요.", date)

        assertTrue(result is SubmitWorryResult.Saved)
        result as SubmitWorryResult.Saved
        assertTrue(result.entry.response.isNotBlank())
        assertNotNull(result.entry.riskLevel)
        assertEquals(result.entry, repository.getByDate(date))
    }

    @Test
    fun 실제_Supabase가_유효하지_않다고_판단한_입력은_Room에_저장하지_않는다() = runBlocking {
        val date = LocalDate.of(2099, 1, 2)

        val result = submitWorry("asdf", date)

        assertTrue(result is SubmitWorryResult.Rejected)
        assertNull(repository.getByDate(date))
    }

    private companion object {
        const val RUN_LIVE_ARGUMENT = "runLiveSupabase"
    }
}
