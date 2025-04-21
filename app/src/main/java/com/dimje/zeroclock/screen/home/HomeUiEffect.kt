package com.dimje.zeroclock.screen.home

import com.dimje.zeroclock.base.BaseUiEffect

sealed class HomeUiEffect : BaseUiEffect {
    data class ShowToast(val message: String): HomeUiEffect()
}