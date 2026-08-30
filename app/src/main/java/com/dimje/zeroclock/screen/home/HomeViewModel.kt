package com.dimje.zeroclock.screen.home

import androidx.lifecycle.viewModelScope
import com.dimje.zeroclock.base.BaseViewModel
import com.dimje.zeroclock.util.KoreaDate
import com.dimje.domain.usecase.ObserveWorriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeWorries: ObserveWorriesUseCase,
) : BaseViewModel<HomeUiState, HomeUiIntent, HomeUiEffect>(HomeUiState()) {
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
            HomeUiIntent.Retry -> observeToday()
        }
    }

    private fun observeToday() {
        observerJob?.cancel()
        observerJob = viewModelScope.launch {
            reduce { copy(isLoading = true, errorMessage = null) }
            observeWorries()
                .catch { error ->
                    reduce { copy(isLoading = false, errorMessage = error.message ?: "기록을 불러오지 못했어요.") }
                }
                .collect { entries ->
                    reduce {
                        copy(
                            isLoading = false,
                            todayEntry = entries.firstOrNull { it.date == KoreaDate.today() },
                            errorMessage = null,
                        )
                    }
                }
        }
    }
}
