package com.dimje.zeroclock.screen.history

import com.dimje.zeroclock.base.BaseUiEffect

sealed interface HistoryUiEffect : BaseUiEffect {
    data object NavigateBack : HistoryUiEffect
    data class OpenDialer(val number: String) : HistoryUiEffect
}
