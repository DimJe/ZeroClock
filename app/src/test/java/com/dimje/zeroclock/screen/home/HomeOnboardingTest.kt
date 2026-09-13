package com.dimje.zeroclock.screen.home

import com.dimje.domain.usecase.CompleteOnboardingUseCase
import com.dimje.domain.usecase.GetOnboardingCompletedUseCase
import com.dimje.domain.usecase.ObserveWorriesUseCase
import com.dimje.zeroclock.testing.FakeDateProvider
import com.dimje.zeroclock.testing.FakeOnboardingRepository
import com.dimje.zeroclock.testing.FakeWorryRepository
import com.dimje.zeroclock.testing.MainDispatcherRule
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeOnboardingTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    private fun viewModel(repository: FakeOnboardingRepository) = HomeViewModel(
        ObserveWorriesUseCase(FakeWorryRepository()),
        FakeDateProvider(LocalDate.of(2026, 9, 13)),
        GetOnboardingCompletedUseCase(repository),
        CompleteOnboardingUseCase(repository),
    )

    @Test
    fun `첫 실행 안내는 별빛까지 다섯 단계를 거쳐 완료되고 다시 표시되지 않는다`() = runTest {
        val repository = FakeOnboardingRepository(completed = false)
        val vm = viewModel(repository)
        advanceUntilIdle()
        assertEquals(HomeGuideStep.MENU, vm.uiState.value.guideStep)
        for (step in HomeGuideStep.entries.drop(1)) {
            vm.onIntent(HomeUiIntent.NextGuide)
            assertEquals(step, vm.uiState.value.guideStep)
            assertEquals(step != HomeGuideStep.STARS, vm.uiState.value.isMenuExpanded)
            assertFalse(repository.completed)
        }
        vm.onIntent(HomeUiIntent.NextGuide)
        advanceUntilIdle()
        assertNull(vm.uiState.value.guideStep)
        assertFalse(vm.uiState.value.isMenuExpanded)
        assertTrue(repository.completed)
        val reopened = viewModel(repository)
        advanceUntilIdle()
        assertNull(reopened.uiState.value.guideStep)
    }

    @Test
    fun `건너뛰기도 완료 상태를 저장한다`() = runTest {
        val repository = FakeOnboardingRepository(completed = false)
        val vm = viewModel(repository)
        advanceUntilIdle()
        vm.onIntent(HomeUiIntent.SkipGuide)
        advanceUntilIdle()
        assertTrue(repository.completed)
        assertNull(vm.uiState.value.guideStep)
    }

    @Test
    fun `저장에 실패하면 안내를 유지하고 재시도할 수 있다`() = runTest {
        val repository = FakeOnboardingRepository(completed = false, failSaving = true)
        val vm = viewModel(repository)
        advanceUntilIdle()
        vm.onIntent(HomeUiIntent.SkipGuide)
        advanceUntilIdle()
        assertEquals(HomeGuideStep.MENU, vm.uiState.value.guideStep)
        assertNotNull(vm.uiState.value.guideError)
        assertFalse(vm.uiState.value.isSavingGuide)
        assertFalse(repository.completed)
        repository.failSaving = false
        vm.onIntent(HomeUiIntent.SkipGuide)
        advanceUntilIdle()
        assertNull(vm.uiState.value.guideStep)
    }

    @Test
    fun `안내 중 메뉴 전환과 이전 단계의 좌표는 무시한다`() = runTest {
        val vm = viewModel(FakeOnboardingRepository(completed = false))
        advanceUntilIdle()
        vm.onIntent(HomeUiIntent.ToggleMenu)
        vm.onIntent(HomeUiIntent.SelectMenu("calendar"))
        assertFalse(vm.uiState.value.isMenuExpanded)
        assertEquals(HomeGuideStep.MENU, vm.uiState.value.guideStep)
        vm.onIntent(
            HomeUiIntent.GuideTargetMeasured(
                HomeGuideStep.CALENDAR,
                androidx.compose.ui.geometry.Rect(0f, 0f, 10f, 10f),
            ),
        )
        assertNull(vm.uiState.value.guideTarget)
    }

    @Test
    fun `설정 읽기 실패는 기록 화면 사용을 막지 않는다`() = runTest {
        val vm = viewModel(FakeOnboardingRepository(failReading = true))
        advanceUntilIdle()
        assertFalse(vm.uiState.value.isLoading)
        assertNull(vm.uiState.value.guideStep)
    }
}
