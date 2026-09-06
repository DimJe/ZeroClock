package com.dimje.zeroclock.screen.detail

import androidx.lifecycle.SavedStateHandle
import com.dimje.domain.model.WorryEntry
import com.dimje.domain.model.WorryRiskLevel
import com.dimje.domain.repository.WorryRepository
import com.dimje.domain.usecase.GetWorryByDateUseCase
import com.dimje.zeroclock.screen.Screen
import com.dimje.zeroclock.testing.FakeWorryRepository
import com.dimje.zeroclock.testing.MainDispatcherRule
import com.dimje.zeroclock.testing.worryEntry
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `유효한 날짜의 기록을 조회해 화면 상태로 제공한다`() = runTest {
        val date = LocalDate.of(2026, 9, 6)
        val expected = worryEntry(1L, date)
        val viewModel = createViewModel(date.toString(), FakeWorryRepository(listOf(expected)))

        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(date, viewModel.uiState.value.date)
        assertEquals(expected, viewModel.uiState.value.entry)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `해당 날짜의 기록이 없으면 안내 오류를 제공한다`() = runTest {
        val date = LocalDate.of(2026, 9, 6)
        val viewModel = createViewModel(date.toString(), FakeWorryRepository())

        advanceUntilIdle()

        assertNull(viewModel.uiState.value.entry)
        assertEquals("해당 날짜의 기록을 찾을 수 없어요.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `날짜 경로가 잘못되면 조회하지 않고 경로 오류를 제공한다`() = runTest {
        val repository = RecordingWorryRepository()
        val viewModel = createViewModel("잘못된-날짜", repository)

        advanceUntilIdle()

        assertFalse(repository.wasRequested)
        assertEquals("잘못된 상세 화면 경로예요.", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `기록 조회에 실패하면 오류 상태를 제공하고 재시도할 수 있다`() = runTest {
        val date = LocalDate.of(2026, 9, 6)
        val expected = worryEntry(1L, date)
        val repository = RecordingWorryRepository(error = IllegalStateException("기록 조회 실패"))
        val viewModel = createViewModel(date.toString(), repository)
        advanceUntilIdle()

        assertEquals("기록 조회 실패", viewModel.uiState.value.errorMessage)

        repository.error = null
        repository.entry = expected
        viewModel.onIntent(DetailUiIntent.Retry)
        advanceUntilIdle()

        assertEquals(expected, viewModel.uiState.value.entry)
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `전화와 뒤로가기 의도는 각각 Effect로 전달한다`() = runTest {
        val date = LocalDate.of(2026, 9, 6)
        val viewModel = createViewModel(date.toString(), FakeWorryRepository())
        advanceUntilIdle()

        viewModel.onIntent(DetailUiIntent.CallSupport("109"))
        viewModel.onIntent(DetailUiIntent.Back)
        advanceUntilIdle()

        assertEquals(DetailUiEffect.OpenDialer("109"), viewModel.effect.first())
        assertEquals(DetailUiEffect.NavigateBack, viewModel.effect.first())
    }

    private fun createViewModel(
        dateArgument: String?,
        repository: WorryRepository,
    ) = DetailViewModel(
        savedStateHandle = SavedStateHandle(mapOf(Screen.Detail.DATE_ARGUMENT to dateArgument)),
        getWorryByDate = GetWorryByDateUseCase(repository),
    )

    private class RecordingWorryRepository(
        var entry: WorryEntry? = null,
        var error: Throwable? = null,
    ) : WorryRepository {
        var wasRequested = false

        override fun observeAll(): Flow<List<WorryEntry>> = emptyFlow()

        override suspend fun getByDate(date: LocalDate): WorryEntry? {
            wasRequested = true
            error?.let { throw it }
            return entry
        }

        override suspend fun save(
            worry: String,
            response: String,
            date: LocalDate,
            riskLevel: WorryRiskLevel,
        ): WorryEntry = throw UnsupportedOperationException("테스트에서 사용하지 않습니다.")
    }
}
