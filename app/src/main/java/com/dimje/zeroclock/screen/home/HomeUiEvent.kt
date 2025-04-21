package com.dimje.zeroclock.screen.home

import com.dimje.zeroclock.base.BaseUiEvent

sealed class HomeUiEvent : BaseUiEvent {
    data object OnRefresh : HomeUiEvent()
    data object OnToastClick : HomeUiEvent()
}