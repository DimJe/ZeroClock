package com.dimje.zeroclock.screen.detail

import com.dimje.zeroclock.base.BaseUiIntent

sealed interface DetailUiIntent : BaseUiIntent {
    data class CallSupport(val number: String) : DetailUiIntent
    data object Retry : DetailUiIntent
    data object Back : DetailUiIntent
}
