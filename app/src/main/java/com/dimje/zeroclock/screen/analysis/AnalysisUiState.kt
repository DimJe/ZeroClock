package com.dimje.zeroclock.screen.analysis

import com.dimje.zeroclock.base.BaseUiState
import com.dimje.domain.model.WorryAnalysis

data class AnalysisUiState(
    val isLoading: Boolean = true,
    val recordedDayCount: Int = 0,
    val analysis: WorryAnalysis? = null,
    val errorMessage: String? = null,
) : BaseUiState {
    val remainingDayCount: Int
        get() = (REQUIRED_DAY_COUNT - recordedDayCount).coerceAtLeast(0)

    val isUnlocked: Boolean
        get() = recordedDayCount >= REQUIRED_DAY_COUNT && analysis != null

    companion object {
        const val REQUIRED_DAY_COUNT = 15
    }
}
