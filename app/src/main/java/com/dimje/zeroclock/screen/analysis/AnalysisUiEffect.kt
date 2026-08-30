package com.dimje.zeroclock.screen.analysis

import com.dimje.zeroclock.base.BaseUiEffect

sealed interface AnalysisUiEffect : BaseUiEffect {
    data object NavigateBack : AnalysisUiEffect
}
