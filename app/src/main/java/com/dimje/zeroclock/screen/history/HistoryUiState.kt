package com.dimje.zeroclock.screen.history

import com.dimje.zeroclock.base.BaseUiState

sealed class HistoryUiState : BaseUiState {
    object Loading : HistoryUiState()
    data class Success(val historyItems: List<String>) : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}