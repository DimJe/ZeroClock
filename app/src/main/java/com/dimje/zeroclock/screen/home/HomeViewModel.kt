package com.dimje.zeroclock.screen.home

import androidx.lifecycle.viewModelScope
import com.dimje.zeroclock.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() :
    BaseViewModel<HomeUiState, HomeUiEvent, HomeUiEffect>() {

    init {
        fetch()
    }

    override fun createInitialState(): HomeUiState = HomeUiState.Loading

    override fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.OnRefresh -> fetch()
            HomeUiEvent.OnToastClick -> postEffect(HomeUiEffect.ShowToast("Home에서 호출됨"))
        }
    }

    private fun fetch() {
        viewModelScope.launch {
            setState(HomeUiState.Loading)
            delay(500) // 임시 로딩 효과
            setState(HomeUiState.Success("홍길동", listOf("알림1", "알림2")))
        }
    }
}