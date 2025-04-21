package com.dimje.zeroclock.screen.home

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.dimje.zeroclock.base.BaseScreenWrapper

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val effect = viewModel.effect.collectAsState(initial = null)
    val context = LocalContext.current

    LaunchedEffect(effect.value) {
        effect.value?.let {
            when (it) {
                is HomeUiEffect.ShowToast -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    BaseScreenWrapper(state = state, onRetry = { viewModel.onEvent(HomeUiEvent.OnRefresh) }) {
        val successState = state as HomeUiState.Success
        Column {
            Text("안녕하세요, ${successState.userName}")
            Button(onClick = { viewModel.onEvent(HomeUiEvent.OnToastClick) }) {
                Text("토스트 메시지")
            }
            // 기타 UI 구성
        }
    }
}