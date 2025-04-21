package com.dimje.zeroclock.screen.ask

import com.dimje.zeroclock.base.BaseUiState

sealed class AskUiState : BaseUiState {
    object Loading : AskUiState()
    data class Success(val questions: List<String>) : AskUiState()
    data class Error(val message: String) : AskUiState()
}