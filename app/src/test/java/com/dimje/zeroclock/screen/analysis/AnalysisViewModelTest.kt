package com.dimje.zeroclock.screen.analysis

import com.dimje.domain.model.WorryEntry
import com.dimje.domain.model.WorryRiskLevel
import com.dimje.domain.repository.WorryRepository
import com.dimje.domain.usecase.AnalyzeWorriesUseCase
import com.dimje.domain.usecase.ObserveWorriesUseCase
import com.dimje.zeroclock.testing.FakeWorryRepository
import com.dimje.zeroclock.testing.MainDispatcherRule
import com.dimje.zeroclock.testing.worryEntry
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnalysisViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `서로 다른 날짜의 기록이 14개면 분석을 잠금 상태로 유지한다`() = runTest {
        val viewModel = createViewModel(FakeWorryRepository(createEntries(14)))

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(14, viewModel.uiState.value.recordedDayCount)
        assertFalse(viewModel.uiState.value.isUnlocked)
        assertNull(viewModel.uiState.value.analysis)
    }

    @Test
    fun `서로 다른 날짜의 기록이 15개면 분석 결과를 제공한다`() = runTest {
        val viewModel = createViewModel(FakeWorryRepository(createEntries(15)))

        advanceUntilIdle()

        assertEquals(15, viewModel.uiState.value.recordedDayCount)
        assertTrue(viewModel.uiState.value.isUnlocked)
        assertEquals("일과 성취에 대한 부담", viewModel.uiState.value.analysis?.mainConcern)
    }

    @Test
    fun `기록 조회에 실패하면 오류 상태를 제공한다`() = runTest {
        val viewModel = createViewModel(FailingWorryRepository("분석용 기록 조회 실패"))

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("분석용 기록 조회 실패", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `뒤로가기 의도는 화면 이동 Effect로 전달한다`() = runTest {
        val viewModel = createViewModel(FakeWorryRepository())
        advanceUntilIdle()

        viewModel.onIntent(AnalysisUiIntent.Back)
        advanceUntilIdle()

        assertEquals(AnalysisUiEffect.NavigateBack, viewModel.effect.first())
    }

    private fun createViewModel(repository: WorryRepository) = AnalysisViewModel(
        observeWorries = ObserveWorriesUseCase(repository),
        analyzeWorries = AnalyzeWorriesUseCase(),
    )

    private fun createEntries(count: Int): List<WorryEntry> = (0 until count).map { index ->
        worryEntry(
            id = index.toLong() + 1L,
            date = LocalDate.of(2026, 1, 1).plusDays(index.toLong()),
        ).copy(worry = "회사 업무와 실수가 걱정돼요.")
    }

    private class FailingWorryRepository(
        private val message: String,
    ) : WorryRepository {
        override fun observeAll(): Flow<List<WorryEntry>> = flow {
            throw IllegalStateException(message)
        }

        override suspend fun getByDate(date: LocalDate): WorryEntry? = null

        override suspend fun save(
            worry: String,
            response: String,
            date: LocalDate,
            riskLevel: WorryRiskLevel,
        ): WorryEntry = error("테스트에서 사용하지 않습니다.")
    }
}
