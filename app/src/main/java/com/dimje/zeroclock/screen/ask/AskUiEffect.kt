package com.dimje.zeroclock.screen.ask

import com.dimje.zeroclock.base.BaseUiEffect

sealed interface AskUiEffect : BaseUiEffect {
    data class ShowMessage(val message: String) : AskUiEffect
    data class OpenDialer(val number: String) : AskUiEffect
    data object NavigateBack : AskUiEffect
}
