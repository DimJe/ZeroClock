package com.dimje.zeroclock.screen.home

import androidx.lifecycle.viewModelScope
import com.dimje.zeroclock.base.BaseViewModel
import com.dimje.domain.time.DateProvider
import com.dimje.domain.usecase.ObserveWorriesUseCase
import com.dimje.domain.usecase.GetOnboardingCompletedUseCase
import com.dimje.domain.usecase.CompleteOnboardingUseCase
import kotlinx.coroutines.CancellationException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeWorries: ObserveWorriesUseCase,
    private val dateProvider: DateProvider,
    private val getOnboardingCompleted: GetOnboardingCompletedUseCase,
    private val completeOnboarding: CompleteOnboardingUseCase,
) : BaseViewModel<HomeUiState, HomeUiIntent, HomeUiEffect>(HomeUiState()) {
    private var currentDate = dateProvider.today()
    private var observerJob: Job? = null
    private var onboardingResolved = false
    private var pendingReminderPermissionAction: ReminderPermissionAction? = null

    init {
        observeToday()
        viewModelScope.launch {
            try {
                if (!getOnboardingCompleted()) reduce { copy(guideStep = HomeGuideStep.MENU) }
                onboardingResolved = true
                showReminderPermissionIfNeeded()
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                // 설정을 읽지 못해도 기존 기록 기능은 사용할 수 있도록 안내를 생략합니다.
            }
        }
    }

    override fun onIntent(intent: HomeUiIntent) {
        when (intent) {
            HomeUiIntent.ToggleMenu -> if (uiState.value.guideStep == null) {
                reduce { copy(isMenuExpanded = !isMenuExpanded) }
            }
            is HomeUiIntent.SelectMenu -> {
                if (uiState.value.guideStep != null) return
                reduce { copy(isMenuExpanded = false) }
                postEffect(HomeUiEffect.Navigate(intent.route))
            }
            HomeUiIntent.AppResumed -> refreshDateIfChanged()
            HomeUiIntent.Retry -> observeToday()
            HomeUiIntent.NextGuide -> {
                val step = uiState.value.guideStep ?: return
                if (uiState.value.isSavingGuide) return
                if (step == HomeGuideStep.entries.last()) finishGuide()
                else reduce {
                    copy(
                        guideStep = HomeGuideStep.entries[step.ordinal + 1],
                        guideTarget = null,
                        isMenuExpanded = HomeGuideStep.entries[step.ordinal + 1] != HomeGuideStep.STARS,
                        guideError = null,
                    )
                }
            }
            HomeUiIntent.SkipGuide -> finishGuide()
            HomeUiIntent.ConfirmReminderPermission -> {
                val action = uiState.value.reminderPermissionAction ?: return
                reduce { copy(reminderPermissionAction = null) }
                postEffect(
                    if (action == ReminderPermissionAction.REQUEST) HomeUiEffect.RequestNotificationPermission
                    else HomeUiEffect.OpenNotificationSettings,
                )
            }
            HomeUiIntent.DismissReminderPermissionInfo -> reduce { copy(reminderPermissionAction = null) }
            is HomeUiIntent.NotificationStatusChanged -> {
                pendingReminderPermissionAction = when {
                    intent.canNotify -> null
                    intent.canRequestRuntimePermission -> ReminderPermissionAction.REQUEST
                    else -> ReminderPermissionAction.SETTINGS
                }
                if (intent.canNotify) reduce { copy(reminderPermissionAction = null) }
                else showReminderPermissionIfNeeded()
            }
            is HomeUiIntent.NotificationPermissionResult -> {
                pendingReminderPermissionAction = if (intent.granted) null else ReminderPermissionAction.SETTINGS
                reduce { copy(reminderPermissionAction = pendingReminderPermissionAction) }
            }
            is HomeUiIntent.GuideTargetMeasured -> if (intent.step == uiState.value.guideStep) {
                reduce { copy(guideTarget = intent.bounds) }
            }
        }
    }

    private fun finishGuide() {
        if (uiState.value.guideStep == null || uiState.value.isSavingGuide) return
        viewModelScope.launch {
            reduce { copy(isSavingGuide = true, guideError = null) }
            try {
                completeOnboarding()
                reduce { copy(guideStep = null, guideTarget = null, isMenuExpanded = false, isSavingGuide = false) }
                showReminderPermissionIfNeeded()
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                reduce { copy(isSavingGuide = false, guideError = "안내 완료 상태를 저장하지 못했어요. 다시 시도해 주세요.") }
            }
        }
    }

    private fun showReminderPermissionIfNeeded() {
        if (onboardingResolved && uiState.value.guideStep == null && uiState.value.reminderPermissionAction == null) {
            reduce { copy(reminderPermissionAction = pendingReminderPermissionAction) }
        }
    }

    private fun refreshDateIfChanged() {
        if (dateProvider.today() != currentDate) observeToday(showLoading = false)
    }

    private fun observeToday(showLoading: Boolean = true) {
        observerJob?.cancel()
        observerJob = viewModelScope.launch {
            reduce {
                if (showLoading) copy(isLoading = true, errorMessage = null)
                else copy(errorMessage = null)
            }
            combine(observeWorries(), dateProvider.observeDateChanges()) { entries, date ->
                val currentMonth = YearMonth.from(date)
                Triple(
                    date,
                    entries.firstOrNull { it.date == date },
                    entries.count { YearMonth.from(it.date) == currentMonth },
                )
            }
                .catch { error ->
                    reduce { copy(isLoading = false, errorMessage = error.message ?: "기록을 불러오지 못했어요.") }
                }
                .collect { (date, todayEntry, monthlyWorryCount) ->
                    currentDate = date
                    reduce {
                        copy(
                            isLoading = false,
                            todayEntry = todayEntry,
                            monthlyWorryCount = monthlyWorryCount,
                            starSeed = date.year * 100L + date.monthValue,
                            errorMessage = null,
                        )
                    }
                }
        }
    }
}
