package com.dimje.zeroclock.screen.home

import com.dimje.zeroclock.base.BaseUiState

sealed class HomeUiState : BaseUiState {
    object Loading : HomeUiState()
    data class Success(val userName: String, val notifications: List<String>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}