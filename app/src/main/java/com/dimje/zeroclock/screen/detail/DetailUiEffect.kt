package com.dimje.zeroclock.screen.detail

import com.dimje.zeroclock.base.BaseUiEffect

sealed interface DetailUiEffect : BaseUiEffect {
    data class OpenDialer(val number: String) : DetailUiEffect
    data object NavigateBack : DetailUiEffect
}
