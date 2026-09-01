package com.dimje.zeroclock.screen.detail

import com.dimje.zeroclock.base.BaseUiEffect

sealed interface DetailUiEffect : BaseUiEffect {
    data object NavigateBack : DetailUiEffect
}
