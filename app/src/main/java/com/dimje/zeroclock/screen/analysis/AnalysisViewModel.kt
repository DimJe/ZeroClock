package com.dimje.zeroclock.screen.analysis

import androidx.lifecycle.viewModelScope
import com.dimje.domain.usecase.AnalyzeWorriesUseCase
import com.dimje.domain.usecase.ObserveWorriesUseCase
import com.dimje.zeroclock.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val observeWorries: ObserveWorriesUseCase,
    private val analyzeWorries: AnalyzeWorriesUseCase,
) : BaseViewModel<AnalysisUiState, AnalysisUiIntent, AnalysisUiEffect>(AnalysisUiState()) {
    init {
        observeAndAnalyze()
    }

    override fun onIntent(intent: AnalysisUiIntent) {
        when (intent) {
            AnalysisUiIntent.Retry -> observeAndAnalyze()
            AnalysisUiIntent.Back -> postEffect(AnalysisUiEffect.NavigateBack)
        }
    }

    private fun observeAndAnalyze() {
        viewModelScope.launch {
            reduce { copy(isLoading = true, errorMessage = null) }
            observeWorries()
                .catch { error ->
                    reduce { copy(isLoading = false, errorMessage = error.message ?: "마음 기록을 분석하지 못했어요.") }
                }
                .collect { entries ->
                    val uniqueCount = entries.distinctBy { it.date }.size
                    reduce {
                        copy(
                            isLoading = false,
                            recordedDayCount = uniqueCount,
                            analysis = analyzeWorries(entries),
                            errorMessage = null,
                        )
                    }
                }
        }
    }
}
