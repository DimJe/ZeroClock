package com.dimje.zeroclock.screen.ask

import androidx.lifecycle.viewModelScope
import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.usecase.AlreadySubmittedTodayException
import com.dimje.domain.usecase.GetWorryByDateUseCase
import com.dimje.domain.usecase.SubmitWorryUseCase
import com.dimje.zeroclock.base.BaseViewModel
import com.dimje.zeroclock.util.KoreaDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AskViewModel @Inject constructor(
    private val getWorryByDate: GetWorryByDateUseCase,
    private val submitWorry: SubmitWorryUseCase,
    private val flowLogger: DataFlowLogger,
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
            val today = KoreaDate.today()
            flowLogger.log(APP_MODULE, "오늘의 고민 조회 시작", "date=$today")
            reduce { copy(isLoading = true, errorMessage = null) }
            runCatching { getWorryByDate(today) }
                .onSuccess { entry ->
                    flowLogger.log(APP_MODULE, "오늘의 고민 조회 완료", "date=$today, found=${entry != null}")
                    reduce { copy(isLoading = false, savedEntry = entry, worry = entry?.worry.orEmpty()) }
                }
                .onFailure { error ->
                    flowLogger.log(APP_MODULE, "오늘의 고민 조회 실패", "error=${error::class.simpleName}")
                    reduce { copy(isLoading = false, errorMessage = error.message ?: "오늘의 기록을 확인하지 못했어요.") }
                }
        }
    }

    private fun submit() {
        val content = uiState.value.worry
        if (content.isBlank() || uiState.value.savedEntry != null || uiState.value.isSubmitting) return

        viewModelScope.launch {
            val today = KoreaDate.today()
            flowLogger.log(APP_MODULE, "고민 제출 시작", "date=$today, worryLength=${content.trim().length}")
            reduce { copy(isSubmitting = true, errorMessage = null) }
            runCatching { submitWorry(content, today) }
                .onSuccess { entry ->
                    flowLogger.log(APP_MODULE, "고민 제출 완료", "entryId=${entry.id}, date=${entry.date}")
                    reduce { copy(isSubmitting = false, savedEntry = entry, worry = entry.worry) }
                    postEffect(AskUiEffect.ShowMessage("오늘의 마음을 안전하게 기록했어요."))
                }
                .onFailure { error ->
                    flowLogger.log(APP_MODULE, "고민 제출 실패", "error=${error::class.simpleName}")
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
        const val APP_MODULE = "APP"
    }
}
