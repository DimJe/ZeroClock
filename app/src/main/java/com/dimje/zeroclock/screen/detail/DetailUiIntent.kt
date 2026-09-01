package com.dimje.zeroclock.screen.detail

import com.dimje.zeroclock.base.BaseUiIntent

sealed interface DetailUiIntent : BaseUiIntent {
    data object Retry : DetailUiIntent
    data object Back : DetailUiIntent
}
