package com.dimje.zeroclock.screen.history

import com.dimje.zeroclock.base.BaseUiEvent

sealed class HistoryUiEvent : BaseUiEvent {
    data object LoadHistory : HistoryUiEvent()
    data class OnItemClick(val itemId: String) : HistoryUiEvent()
}