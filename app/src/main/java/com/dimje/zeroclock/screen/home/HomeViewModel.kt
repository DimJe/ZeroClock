package com.dimje.zeroclock.screen.home

import androidx.lifecycle.viewModelScope
import com.dimje.zeroclock.base.BaseViewModel
import com.dimje.domain.time.DateProvider
import com.dimje.domain.usecase.ObserveWorriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeWorries: ObserveWorriesUseCase,
    private val dateProvider: DateProvider,
) : BaseViewModel<HomeUiState, HomeUiIntent, HomeUiEffect>(HomeUiState()) {
    private var currentDate = dateProvider.today()
    private var observerJob: Job? = null

    init {
        observeToday()
    }

    override fun onIntent(intent: HomeUiIntent) {
        when (intent) {
            HomeUiIntent.ToggleMenu -> reduce { copy(isMenuExpanded = !isMenuExpanded) }
            is HomeUiIntent.SelectMenu -> {
                reduce { copy(isMenuExpanded = false) }
                postEffect(HomeUiEffect.Navigate(intent.route))
            }
            HomeUiIntent.AppResumed -> refreshDateIfChanged()
            HomeUiIntent.Retry -> observeToday()
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
                date to entries.firstOrNull { it.date == date }
            }
                .catch { error ->
                    reduce { copy(isLoading = false, errorMessage = error.message ?: "기록을 불러오지 못했어요.") }
                }
                .collect { (date, todayEntry) ->
                    currentDate = date
                    reduce {
                        copy(
                            isLoading = false,
                            todayEntry = todayEntry,
                            errorMessage = null,
                        )
                    }
                }
        }
    }
}
