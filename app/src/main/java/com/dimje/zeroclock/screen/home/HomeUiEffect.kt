package com.dimje.zeroclock.screen.home

import com.dimje.zeroclock.base.BaseUiEffect

sealed interface HomeUiEffect : BaseUiEffect {
    data class Navigate(val route: String) : HomeUiEffect
}
