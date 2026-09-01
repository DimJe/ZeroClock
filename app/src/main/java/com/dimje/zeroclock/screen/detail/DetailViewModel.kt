package com.dimje.zeroclock.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dimje.domain.usecase.GetWorryByDateUseCase
import com.dimje.zeroclock.base.BaseViewModel
import com.dimje.zeroclock.screen.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getWorryByDate: GetWorryByDateUseCase,
) : BaseViewModel<DetailUiState, DetailUiIntent, DetailUiEffect>(DetailUiState()) {
    private val date = savedStateHandle.get<String>(Screen.Detail.DATE_ARGUMENT)
        ?.let { value -> runCatching { LocalDate.parse(value) }.getOrNull() }

    init {
        loadEntry()
    }

    override fun onIntent(intent: DetailUiIntent) {
        when (intent) {
            DetailUiIntent.Retry -> loadEntry()
            DetailUiIntent.Back -> postEffect(DetailUiEffect.NavigateBack)
        }
    }

    private fun loadEntry() {
        val targetDate = date
        if (targetDate == null) {
            reduce {
                copy(
                    isLoading = false,
                    errorMessage = "잘못된 상세 화면 경로예요.",
                )
            }
            return
        }

        viewModelScope.launch {
            reduce { copy(isLoading = true, date = targetDate, errorMessage = null) }
            runCatching { getWorryByDate(targetDate) }
                .onSuccess { entry ->
                    reduce {
                        copy(
                            isLoading = false,
                            entry = entry,
                            errorMessage = if (entry == null) "해당 날짜의 기록을 찾을 수 없어요." else null,
                        )
                    }
                }
                .onFailure { error ->
                    reduce {
                        copy(
                            isLoading = false,
                            errorMessage = error.message ?: "기록을 불러오지 못했어요.",
                        )
                    }
                }
        }
    }
}
