package com.dimje.data.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dimje.data.local.ZeroClockDatabase
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomWorryRepositoryTest {
    private lateinit var database: ZeroClockDatabase
    private lateinit var repository: RoomWorryRepository

    @Before
    fun 데이터베이스를_준비한다() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ZeroClockDatabase::class.java,
        ).allowMainThreadQueries().build()
        repository = RoomWorryRepository(database.worryDao())
    }

    @After
    fun 데이터베이스를_닫는다() {
        database.close()
    }

    @Test
    fun 고민을_저장하면_같은_날짜로_조회할_수_있다() = runBlocking {
        val date = LocalDate.of(2026, 8, 31)

        val saved = repository.save(
            worry = "내일 발표가 걱정돼요.",
            response = "오늘은 충분히 쉬어도 괜찮아요.",
            date = date,
        )
        val found = repository.getByDate(date)

        assertNotNull(found)
        assertEquals(saved.id, found?.id)
        assertEquals("내일 발표가 걱정돼요.", found?.worry)
        assertEquals("오늘은 충분히 쉬어도 괜찮아요.", found?.response)
        assertEquals(date, found?.date)
    }

    @Test
    fun 전체_기록은_최근_날짜부터_조회된다() = runBlocking {
        val olderDate = LocalDate.of(2026, 8, 30)
        val newerDate = LocalDate.of(2026, 8, 31)
        repository.save("첫 번째 고민", "첫 번째 답변", olderDate)
        repository.save("두 번째 고민", "두 번째 답변", newerDate)

        val entries = repository.observeAll().first()

        assertEquals(2, entries.size)
        assertEquals(newerDate, entries[0].date)
        assertEquals(olderDate, entries[1].date)
    }

    @Test
    fun 같은_날짜에는_두_개의_고민을_저장할_수_없다() = runBlocking {
        val date = LocalDate.of(2026, 8, 31)
        repository.save("첫 번째 고민", "첫 번째 답변", date)

        val result = runCatching {
            repository.save("두 번째 고민", "두 번째 답변", date)
        }

        assertTrue(result.exceptionOrNull() is SQLiteConstraintException)
        assertEquals(1, repository.observeAll().first().size)
    }
}
