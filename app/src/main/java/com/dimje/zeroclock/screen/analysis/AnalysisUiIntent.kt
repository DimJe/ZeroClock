package com.dimje.zeroclock.screen.analysis

import com.dimje.zeroclock.base.BaseUiIntent

sealed interface AnalysisUiIntent : BaseUiIntent {
    data object Retry : AnalysisUiIntent
    data object Back : AnalysisUiIntent
}
