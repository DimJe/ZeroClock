package com.dimje.zeroclock.screen.history

import androidx.lifecycle.viewModelScope
import com.dimje.zeroclock.base.BaseViewModel
import com.dimje.zeroclock.util.KoreaDate
import com.dimje.domain.usecase.ObserveWorriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.YearMonth
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val observeWorries: ObserveWorriesUseCase,
) : BaseViewModel<HistoryUiState, HistoryUiIntent, HistoryUiEffect>(
    HistoryUiState(
        visibleMonth = YearMonth.from(KoreaDate.today()),
        selectedDate = KoreaDate.today(),
    ),
) {
    init {
        observeHistory()
    }

    override fun onIntent(intent: HistoryUiIntent) {
        when (intent) {
            HistoryUiIntent.PreviousMonth -> reduce { copy(visibleMonth = visibleMonth.minusMonths(1)) }
            HistoryUiIntent.NextMonth -> reduce { copy(visibleMonth = visibleMonth.plusMonths(1)) }
            is HistoryUiIntent.SelectDate -> reduce { copy(selectedDate = intent.date) }
            is HistoryUiIntent.OpenDetail -> postEffect(HistoryUiEffect.NavigateToDetail(intent.date))
            HistoryUiIntent.Retry -> observeHistory()
            HistoryUiIntent.Back -> postEffect(HistoryUiEffect.NavigateBack)
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
