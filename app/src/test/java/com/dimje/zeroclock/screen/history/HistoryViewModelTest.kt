package com.dimje.zeroclock.screen.history

import com.dimje.domain.usecase.ObserveWorriesUseCase
import com.dimje.zeroclock.testing.FakeDateProvider
import com.dimje.zeroclock.testing.FakeWorryRepository
import com.dimje.zeroclock.testing.MainDispatcherRule
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `오늘을 보고 있을 때 자정이 지나면 다음 달과 날짜를 선택한다`() = runTest {
        val lastDay = LocalDate.of(2026, 9, 30)
        val nextDay = lastDay.plusDays(1)
        val dateProvider = FakeDateProvider(lastDay)
        val viewModel = HistoryViewModel(
            observeWorries = ObserveWorriesUseCase(FakeWorryRepository()),
            dateProvider = dateProvider,
        )

        advanceUntilIdle()
        dateProvider.moveTo(nextDay)
        advanceUntilIdle()

        assertEquals(YearMonth.of(2026, 10), viewModel.uiState.value.visibleMonth)
        assertEquals(nextDay, viewModel.uiState.value.selectedDate)
    }
}
