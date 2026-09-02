package com.dimje.zeroclock.screen.ask

import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.model.ComfortResponseResult
import com.dimje.domain.usecase.GetWorryByDateUseCase
import com.dimje.domain.usecase.SubmitWorryUseCase
import com.dimje.zeroclock.testing.FakeComfortResponseRepository
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AskViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `같은 날짜에 화면으로 복귀하면 로딩 상태로 돌아가지 않는다`() = runTest {
        val date = LocalDate.of(2026, 9, 2)
        val dateProvider = FakeDateProvider(date)
        val repository = FakeWorryRepository(listOf(worryEntry(1, date)))
        val viewModel = AskViewModel(
            getWorryByDate = GetWorryByDateUseCase(repository),
            submitWorry = SubmitWorryUseCase(repository, FakeComfortResponseRepository()),
            flowLogger = DataFlowLogger.NONE,
            dateProvider = dateProvider,
        )

        advanceUntilIdle()
        viewModel.onIntent(AskUiIntent.AppResumed)

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(date, viewModel.uiState.value.savedEntry?.date)
    }

    @Test
    fun `자정이 지나면 작성 화면이 새 날짜의 빈 상태로 갱신된다`() = runTest {
        val firstDate = LocalDate.of(2026, 9, 1)
        val dateProvider = FakeDateProvider(firstDate)
        val repository = FakeWorryRepository(listOf(worryEntry(1, firstDate)))
        val viewModel = AskViewModel(
            getWorryByDate = GetWorryByDateUseCase(repository),
            submitWorry = SubmitWorryUseCase(repository, FakeComfortResponseRepository()),
            flowLogger = DataFlowLogger.NONE,
            dateProvider = dateProvider,
        )

        advanceUntilIdle()
        assertEquals(firstDate, viewModel.uiState.value.savedEntry?.date)

        dateProvider.moveTo(firstDate.plusDays(1))
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.savedEntry)
        assertEquals("", viewModel.uiState.value.worry)
    }

    @Test
    fun `유효하지 않은 입력은 저장하지 않고 입력과 알림을 유지한다`() = runTest {
        val date = LocalDate.of(2026, 9, 2)
        val repository = FakeWorryRepository()
        val viewModel = AskViewModel(
            getWorryByDate = GetWorryByDateUseCase(repository),
            submitWorry = SubmitWorryUseCase(
                repository,
                FakeComfortResponseRepository(
                    result = ComfortResponseResult.Invalid("고민 내용을 조금 더 구체적으로 적어 주세요."),
                ),
            ),
            flowLogger = DataFlowLogger.NONE,
            dateProvider = FakeDateProvider(date),
        )
        advanceUntilIdle()

        viewModel.onIntent(AskUiIntent.WorryChanged("asdf"))
        viewModel.onIntent(AskUiIntent.Submit)
        advanceUntilIdle()

        assertEquals("asdf", viewModel.uiState.value.worry)
        assertNull(viewModel.uiState.value.savedEntry)
        assertTrue(viewModel.uiState.value.alert?.message?.contains("구체적으로") == true)
        assertNull(repository.getByDate(date))
    }
}
