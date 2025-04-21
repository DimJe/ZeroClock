package com.dimje.zeroclock.screen.history

import com.dimje.zeroclock.base.BaseUiEffect

sealed class HistoryUiEffect : BaseUiEffect {
    data class ShowToast(val message: String) : HistoryUiEffect()
    data class NavigateToDetail(val itemJson: String) : HistoryUiEffect()
}
