package com.dimje.zeroclock.screen.history

import androidx.lifecycle.viewModelScope
import com.dimje.zeroclock.base.BaseViewModel
import com.dimje.domain.time.DateProvider
import com.dimje.domain.usecase.ObserveWorriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.YearMonth
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val observeWorries: ObserveWorriesUseCase,
    private val dateProvider: DateProvider,
) : BaseViewModel<HistoryUiState, HistoryUiIntent, HistoryUiEffect>(
    HistoryUiState(
        visibleMonth = YearMonth.from(dateProvider.today()),
        selectedDate = dateProvider.today(),
    ),
) {
    private var currentDate = dateProvider.today()
    private var dateObserverJob: Job? = null

    init {
        observeHistory()
        observeDateChanges()
    }

    override fun onIntent(intent: HistoryUiIntent) {
        when (intent) {
            HistoryUiIntent.PreviousMonth -> reduce { copy(visibleMonth = visibleMonth.minusMonths(1)) }
            HistoryUiIntent.NextMonth -> reduce { copy(visibleMonth = visibleMonth.plusMonths(1)) }
            is HistoryUiIntent.SelectDate -> reduce { copy(selectedDate = intent.date) }
            is HistoryUiIntent.OpenDetail -> postEffect(HistoryUiEffect.NavigateToDetail(intent.date))
            HistoryUiIntent.AppResumed -> refreshDateIfChanged()
            HistoryUiIntent.Retry -> observeHistory()
            HistoryUiIntent.Back -> postEffect(HistoryUiEffect.NavigateBack)
        }
    }

    private fun refreshDateIfChanged() {
        if (dateProvider.today() != currentDate) observeDateChanges()
    }

    private fun observeDateChanges() {
        dateObserverJob?.cancel()
        dateObserverJob = viewModelScope.launch {
            dateProvider.observeDateChanges().collect { newDate ->
                if (newDate == currentDate) return@collect
                val wasFollowingToday = uiState.value.selectedDate == currentDate &&
                    uiState.value.visibleMonth == YearMonth.from(currentDate)
                currentDate = newDate
                if (wasFollowingToday) {
                    reduce {
                        copy(
                            visibleMonth = YearMonth.from(newDate),
                            selectedDate = newDate,
                        )
                    }
                }
            }
        }
    }

    private fun observeHistory() {
        viewModelScope.launch {
            reduce { copy(isLoading = true, errorMessage = null) }
            observeWorries()
                .catch { error ->
                    reduce { copy(isLoading = false, errorMessage = error.message ?: "기록을 불러오지 못했어요.") }
                }
                .collect { entries ->
                    reduce { copy(isLoading = false, entries = entries, errorMessage = null) }
                }
        }
    }
}
