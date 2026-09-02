package com.dimje.zeroclock.screen.ask

import androidx.lifecycle.viewModelScope
import com.dimje.domain.logging.DataFlowLogger
import com.dimje.domain.model.RejectionReason
import com.dimje.domain.model.SubmitWorryResult
import com.dimje.domain.model.WorryEntry
import com.dimje.domain.time.DateProvider
import com.dimje.domain.usecase.AlreadySubmittedTodayException
import com.dimje.domain.usecase.GetWorryByDateUseCase
import com.dimje.domain.usecase.SubmitWorryUseCase
import com.dimje.zeroclock.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AskViewModel @Inject constructor(
    private val getWorryByDate: GetWorryByDateUseCase,
    private val submitWorry: SubmitWorryUseCase,
    private val flowLogger: DataFlowLogger,
    private val dateProvider: DateProvider,
) : BaseViewModel<AskUiState, AskUiIntent, AskUiEffect>(AskUiState()) {
    private var currentDate = dateProvider.today()
    private var hasLoaded = false
    private var dateObserverJob: Job? = null
    private var loadJob: Job? = null

    init {
        observeDateChanges()
    }

    override fun onIntent(intent: AskUiIntent) {
        when (intent) {
            is AskUiIntent.WorryChanged -> reduce {
                if (savedEntry == null) copy(worry = intent.worry.take(MAX_WORRY_LENGTH)) else this
            }
            AskUiIntent.Submit -> submit()
            AskUiIntent.AppResumed -> refreshDateIfChanged()
            AskUiIntent.DismissAlert -> reduce { copy(alert = null) }
            is AskUiIntent.CallSupport -> postEffect(AskUiEffect.OpenDialer(intent.number))
            AskUiIntent.Retry -> loadToday()
            AskUiIntent.Back -> postEffect(AskUiEffect.NavigateBack)
        }
    }

    private fun loadToday() {
        loadDate(dateProvider.today(), showLoading = true)
    }

    private fun refreshDateIfChanged() {
        if (dateProvider.today() != currentDate) observeDateChanges()
    }

    private fun observeDateChanges() {
        dateObserverJob?.cancel()
        dateObserverJob = viewModelScope.launch {
            dateProvider.observeDateChanges().collect(::loadDate)
        }
    }

    private fun loadDate(today: LocalDate) {
        loadDate(today, showLoading = !hasLoaded)
    }

    private fun loadDate(today: LocalDate, showLoading: Boolean) {
        currentDate = today
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            flowLogger.log(APP_MODULE, "오늘의 고민 조회 시작", "date=$today")
            reduce {
                if (showLoading) copy(isLoading = true, errorMessage = null)
                else copy(errorMessage = null)
            }
            runCatching { getWorryByDate(today) }
                .onSuccess { entry ->
                    hasLoaded = true
                    flowLogger.log(APP_MODULE, "오늘의 고민 조회 완료", "date=$today, found=${entry != null}")
                    reduce { copy(isLoading = false, savedEntry = entry, worry = entry?.worry.orEmpty()) }
                }
                .onFailure { error ->
                    hasLoaded = true
                    flowLogger.log(APP_MODULE, "오늘의 고민 조회 실패", "error=${error::class.simpleName}")
                    reduce { copy(isLoading = false, errorMessage = error.message ?: "오늘의 기록을 확인하지 못했어요.") }
                }
        }
    }

    private fun submit() {
        val content = uiState.value.worry
        if (content.isBlank() || uiState.value.savedEntry != null || uiState.value.isSubmitting) return

        viewModelScope.launch {
            val today = dateProvider.today()
            flowLogger.log(APP_MODULE, "고민 제출 시작", "date=$today, worryLength=${content.trim().length}")
            reduce { copy(isSubmitting = true, errorMessage = null, alert = null) }
            runCatching { submitWorry(content, today) }
                .onSuccess { result ->
                    when (result) {
                        is SubmitWorryResult.Saved -> handleSavedEntry(result.entry)
                        is SubmitWorryResult.Rejected -> {
                            flowLogger.log(
                                APP_MODULE,
                                "고민 제출 제외",
                                "reason=${result.reason}",
                            )
                            val title = when (result.reason) {
                                RejectionReason.INVALID -> "입력 내용을 확인해 주세요"
                                RejectionReason.UNKNOWN -> "내용을 확인하지 못했어요"
                            }
                            reduce {
                                copy(
                                    isSubmitting = false,
                                    alert = AskAlert(title = title, message = result.message),
                                )
                            }
                        }
                    }
                }
                .onFailure { error ->
                    flowLogger.log(APP_MODULE, "고민 제출 실패", "error=${error::class.simpleName}")
                    val message = if (error is AlreadySubmittedTodayException) {
                        "오늘은 이미 마음을 기록했어요."
                    } else {
                        error.message ?: "기록을 저장하지 못했어요."
                    }
                    reduce {
                        copy(
                            isSubmitting = false,
                            alert = AskAlert(
                                title = if (error is AlreadySubmittedTodayException) {
                                    "오늘의 기록을 확인해 주세요"
                                } else {
                                    "답변을 불러오지 못했어요"
                                },
                                message = message,
                            ),
                        )
                    }
                }
        }
    }

    private fun handleSavedEntry(entry: WorryEntry) {
        flowLogger.log(
            APP_MODULE,
            "고민 제출 완료",
            "entryId=${entry.id}, date=${entry.date}, riskLevel=${entry.riskLevel}",
        )
        if (entry.date == dateProvider.today()) {
            reduce { copy(isSubmitting = false, savedEntry = entry, worry = entry.worry) }
        } else {
            reduce { copy(isSubmitting = false) }
            loadToday()
        }
        postEffect(AskUiEffect.ShowMessage("오늘의 마음을 안전하게 기록했어요."))
    }

    private companion object {
        const val MAX_WORRY_LENGTH = 1_000
        const val APP_MODULE = "APP"
    }
}
