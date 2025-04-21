package com.dimje.zeroclock.screen.ask

import com.dimje.zeroclock.base.BaseUiEffect

sealed class AskUiEffect : BaseUiEffect {
    data class ShowToast(val message: String) : AskUiEffect()
}