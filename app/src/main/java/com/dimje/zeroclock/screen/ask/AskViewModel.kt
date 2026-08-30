package com.dimje.zeroclock.screen.ask

import androidx.lifecycle.viewModelScope
import com.dimje.zeroclock.base.BaseViewModel
import com.dimje.zeroclock.util.KoreaDate
import com.dimje.domain.usecase.AlreadySubmittedTodayException
import com.dimje.domain.usecase.GetWorryByDateUseCase
import com.dimje.domain.usecase.SubmitWorryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AskViewModel @Inject constructor(
    private val getWorryByDate: GetWorryByDateUseCase,
    private val submitWorry: SubmitWorryUseCase,
) : BaseViewModel<AskUiState, AskUiIntent, AskUiEffect>(AskUiState()) {
    init {
        loadToday()
    }

    override fun onIntent(intent: AskUiIntent) {
        when (intent) {
            is AskUiIntent.WorryChanged -> reduce {
                if (savedEntry == null) copy(worry = intent.worry.take(MAX_WORRY_LENGTH)) else this
            }
            AskUiIntent.Submit -> submit()
            AskUiIntent.Retry -> loadToday()
            AskUiIntent.Back -> postEffect(AskUiEffect.NavigateBack)
        }
    }

    private fun loadToday() {
        viewModelScope.launch {
            reduce { copy(isLoading = true, errorMessage = null) }
            runCatching { getWorryByDate(KoreaDate.today()) }
                .onSuccess { entry ->
                    reduce { copy(isLoading = false, savedEntry = entry, worry = entry?.worry.orEmpty()) }
                }
                .onFailure { error ->
                    reduce { copy(isLoading = false, errorMessage = error.message ?: "오늘의 기록을 확인하지 못했어요.") }
                }
        }
    }

    private fun submit() {
        val content = uiState.value.worry
        if (content.isBlank() || uiState.value.savedEntry != null || uiState.value.isSubmitting) return

        viewModelScope.launch {
            reduce { copy(isSubmitting = true, errorMessage = null) }
            runCatching { submitWorry(content, KoreaDate.today()) }
                .onSuccess { entry ->
                    reduce { copy(isSubmitting = false, savedEntry = entry, worry = entry.worry) }
                    postEffect(AskUiEffect.ShowMessage("오늘의 마음을 안전하게 기록했어요."))
                }
                .onFailure { error ->
                    val message = if (error is AlreadySubmittedTodayException) {
                        "오늘은 이미 마음을 기록했어요."
                    } else {
                        error.message ?: "기록을 저장하지 못했어요."
                    }
                    reduce { copy(isSubmitting = false, errorMessage = message) }
                    postEffect(AskUiEffect.ShowMessage(message))
                }
        }
    }

    private companion object {
        const val MAX_WORRY_LENGTH = 1_000
    }
}
