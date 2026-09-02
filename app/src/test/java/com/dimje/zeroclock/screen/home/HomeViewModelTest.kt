package com.dimje.zeroclock.screen.home

import com.dimje.domain.usecase.ObserveWorriesUseCase
import com.dimje.zeroclock.testing.FakeDateProvider
import com.dimje.zeroclock.testing.FakeWorryRepository
import com.dimje.zeroclock.testing.MainDispatcherRule
import com.dimje.zeroclock.testing.worryEntry
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `같은 날짜에 화면으로 복귀하면 로딩 상태로 돌아가지 않는다`() = runTest {
        val date = LocalDate.of(2026, 9, 2)
        val dateProvider = FakeDateProvider(date)
        val viewModel = HomeViewModel(
            observeWorries = ObserveWorriesUseCase(FakeWorryRepository(listOf(worryEntry(1, date)))),
            dateProvider = dateProvider,
        )

        advanceUntilIdle()
        viewModel.onIntent(HomeUiIntent.AppResumed)

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(date, viewModel.uiState.value.todayEntry?.date)
    }

    @Test
    fun `자정이 지나면 홈 화면의 오늘 기록이 새 날짜 기준으로 갱신된다`() = runTest {
        val firstDate = LocalDate.of(2026, 9, 1)
        val nextDate = firstDate.plusDays(1)
        val dateProvider = FakeDateProvider(firstDate)
        val repository = FakeWorryRepository(listOf(worryEntry(1, firstDate), worryEntry(2, nextDate)))
        val viewModel = HomeViewModel(ObserveWorriesUseCase(repository), dateProvider)

        advanceUntilIdle()
        assertEquals(firstDate, viewModel.uiState.value.todayEntry?.date)

        dateProvider.moveTo(nextDate)
        advanceUntilIdle()

        assertEquals(nextDate, viewModel.uiState.value.todayEntry?.date)
    }
}
